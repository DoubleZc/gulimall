package com.zcx.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.comm.CosException;
import com.zcx.common.constant.OrderConstant;
import com.zcx.common.to.MemberTo;
import com.zcx.common.utils.*;
import com.zcx.gulimall.order.dao.OrderDao;
import com.zcx.gulimall.order.entity.OrderEntity;
import com.zcx.gulimall.order.entity.OrderItemEntity;
import com.zcx.gulimall.order.feign.CartFeignService;
import com.zcx.gulimall.order.feign.MemberFeignService;
import com.zcx.gulimall.order.feign.ProductFeignService;
import com.zcx.gulimall.order.feign.WareFeignService;
import com.zcx.gulimall.order.service.OrderItemService;
import com.zcx.gulimall.order.service.OrderService;
import com.zcx.gulimall.order.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService
{
	
	@Autowired
	MemberFeignService memberFeignService;
	
	@Autowired
	CartFeignService cartFeignService;
	@Autowired
	WareFeignService wareFeignService;
	@Autowired
	ProductFeignService productFeignService;
	
	
	@Autowired
	OrderItemService orderItemService;
	
	
	@Autowired
	StringRedisTemplate redisTemplate;
	
	@Autowired
	ThreadPoolExecutor executor;
	
	
	@Override
	public PageUtils queryPage(Map<String, Object> params)
	{
		IPage<OrderEntity> page = this.page(
				new Query<OrderEntity>().getPage(params),
				new QueryWrapper<OrderEntity>()
		);
		
		return new PageUtils(page);
	}
	
	@Override
	public OrderConfirmVo confirm()
	{
		OrderConfirmVo vo = new OrderConfirmVo();
		MemberTo msg = MyThreadMsg.getMsg(MemberTo.class);
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		
		
		CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
			RequestContextHolder.setRequestAttributes(requestAttributes);
			List<MemberAddressVo> memberAddressVos = memberFeignService.listAddress(msg.getId());
			vo.setAddressVoList(memberAddressVos);
			vo.setIntegration(msg.getIntegration());
			
			//存入令牌redis
			String token = UUID.randomUUID().toString().replace("-", "");
			vo.setOrderToken(token);
			redisTemplate.opsForValue().set(OrderConstant.ORDER_TOKEN_PREFIX + msg.getId(), token, 30, TimeUnit.MINUTES);
		}, executor);
		
		
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			RequestContextHolder.setRequestAttributes(requestAttributes);
			List<OrderItemVo> listcart = cartFeignService.listcart();
			vo.setItems(listcart);
		}, executor).thenRunAsync(() -> {
			List<OrderItemVo> items = vo.getItems();
			List<Long> skuIds = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
			R r = wareFeignService.getBySkuIds(skuIds);
			Map<Long, Integer> map = r.getData("data", new TypeReference<Map<Long, Integer>>()
			{
			});
			if (!map.isEmpty()) {
				items.forEach(orderItemVo -> {
					orderItemVo.setHasStock(map.get(orderItemVo.getSkuId()) > 0);
				});
			}
		}, executor);
		
		
		try {
			CompletableFuture.allOf(future, future1).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}
	
	
	@Transactional
	@Override
	public R submitOrder(OrderSubmitVo vo) throws CosException.NotStock
	{
		//验证令牌
		String orderToken = vo.getOrderToken();
		MemberTo msg = MyThreadMsg.getMsg(MemberTo.class);
		String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
		String key = OrderConstant.ORDER_TOKEN_PREFIX + msg.getId();
		//原子性 拿取，对比，删除token 确保执行一次
		Long execute = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
				Collections.singletonList(key), orderToken);
		
		if (1 == execute) {
			OrderCreatTo orderCreatTo = creatOrder(vo);
			BigDecimal payAmount = orderCreatTo.getOrderEntity().getPayAmount();
			BigDecimal payPrice = vo.getPayPrice();
			BigDecimal abs = payAmount.subtract(payPrice).abs();
			
		
			
			if (abs.doubleValue()<0.01)
			{
				//保存订单
				saveOrder(orderCreatTo);
				
				//锁库存
				WareSkuLockTo wareSkuLockTo = new WareSkuLockTo();
				wareSkuLockTo.setOrderSn(orderCreatTo.getOrderEntity().getOrderSn());
				List<OrderItemVo> collect = orderCreatTo.getItems().stream().map(i -> {
					OrderItemVo orderItemVo = new OrderItemVo();
					orderItemVo.setSkuId(i.getSkuId());
					orderItemVo.setCount(i.getSkuQuantity());
					return orderItemVo;
				}).collect(Collectors.toList());
				wareSkuLockTo.setItemVo(collect);

				
				R r = wareFeignService.lockStock(wareSkuLockTo);
				if (r.getCode()==0)
				{
					r.put("orderSn",orderCreatTo.getOrderEntity().getOrderSn());
					r.put("price",payAmount);
					return r;
					
				}else
				{
					throw new CosException.NotStock(ExceptionCode.ORDER_NOT_STOCK,r.getData("data",new TypeReference<List<Long>>(){}));
				}
				
			}else
			{
				return R.error(ExceptionCode.ORDER_DIFFERENT_PRICE);
			}
		} else {
			return R.error(ExceptionCode.ORDER_SUBMIT_ERROR);
		}
		
		
	}
	
	
	private void saveOrder(OrderCreatTo orderCreatTo)
	{
		OrderEntity orderEntity = orderCreatTo.getOrderEntity();
		save(orderEntity);
		
		
		List<OrderItemEntity> items = orderCreatTo.getItems();
		orderItemService.saveBatch(items);
		
		
	}
	
	
	private OrderCreatTo creatOrder(OrderSubmitVo vo)
	{
		OrderCreatTo orderCreatTo = new OrderCreatTo();
		
		OrderEntity orderEntity = builderOrder(vo.getAddrId());
		orderCreatTo.setOrderEntity(orderEntity);
		
		
		List<OrderItemEntity> orderItemEntities = builderOrderItem(orderEntity.getOrderSn());
		orderCreatTo.setItems(orderItemEntities);
		
		
		computerPrice(orderItemEntities, orderEntity);
		
		return orderCreatTo;
	}
	
	private void computerPrice(List<OrderItemEntity> orderItemEntities, OrderEntity orderEntity)
	{
		BigDecimal coupon = new BigDecimal("0");
		BigDecimal integration = new BigDecimal("0");
		BigDecimal promotion = new BigDecimal("0");
		BigDecimal price = new BigDecimal("0");
		Integer growth=0 ;
		Integer giftIntegration=0 ;

		
		
		for (OrderItemEntity i : orderItemEntities) {
			price = price.add(i.getRealAmount());
			coupon = coupon.add(i.getCouponAmount());
			integration = integration.add(i.getIntegrationAmount());
			promotion = promotion.add(i.getPromotionAmount());
			
			growth +=i.getGiftGrowth();
			giftIntegration +=i.getGiftIntegration();
			
			
		}
		
		

		orderEntity.setTotalAmount(price);
		orderEntity.setPayAmount(price.add(orderEntity.getFreightAmount()));
		orderEntity.setCouponAmount(coupon);
		orderEntity.setIntegrationAmount(integration);
		orderEntity.setPromotionAmount(promotion);
	}
	
	
	private OrderEntity builderOrder(Long addrId)
	{
		OrderEntity orderEntity = new OrderEntity();
		String timeId = IdWorker.getTimeId();
		orderEntity.setOrderSn(timeId);
		R fare = wareFeignService.getFare(addrId);
		FareVo fareVo = fare.getData("data", FareVo.class);
		orderEntity.setFreightAmount(fareVo.getFare());
		orderEntity.setReceiverName(fareVo.getAddress().getName());
		orderEntity.setReceiverDetailAddress(fareVo.getAddress().getDetailAddress());
		orderEntity.setReceiverPhone(fareVo.getAddress().getPhone());
		orderEntity.setReceiverProvince(fareVo.getAddress().getProvince());
		orderEntity.setMemberId(fareVo.getAddress().getMemberId());
		
		orderEntity.setStatus(OrderConstant.OrderStatusEnum.CREATE_NEW.getCode());
		orderEntity.setAutoConfirmDay(7);
		
		orderEntity.setDeleteStatus(0);
		
		
		return orderEntity;
	}
	
	
	private List<OrderItemEntity> builderOrderItem(String token)
	{
		List<OrderItemVo> cart = cartFeignService.listcart();
		if (cart != null && !cart.isEmpty()) {
			return cart.stream().map(i -> {
				OrderItemEntity orderItemEntity = new OrderItemEntity();
				//sku信息
				orderItemEntity.setOrderSn(token);
				orderItemEntity.setSkuId(i.getSkuId());
				orderItemEntity.setSkuName(i.getTitle());
				orderItemEntity.setSkuPic(i.getImage());
				orderItemEntity.setSkuPrice(i.getPrice());
				
				orderItemEntity.setSkuQuantity(i.getCount());
				orderItemEntity.setSkuAttrsVals(String.join(",", i.getSkuAttr()));
				//积分信息
				orderItemEntity.setGiftGrowth(i.getPrice().intValue());
				orderItemEntity.setGiftIntegration(i.getPrice().intValue());
				
				//spu信息
				R spuInfo = productFeignService.getSpuInfo(i.getSkuId());
				SpuInfoVo data = spuInfo.getData("data", SpuInfoVo.class);
				
				orderItemEntity.setSpuId(data.getId());
				orderItemEntity.setSpuName(data.getSpuName());
				orderItemEntity.setSpuBrand(data.getBrandId().toString());
				orderItemEntity.setCategoryId(data.getCatalogId());
				
				orderItemEntity.setPromotionAmount(new BigDecimal("0"));
				orderItemEntity.setCouponAmount(new BigDecimal("0"));
				orderItemEntity.setIntegrationAmount(new BigDecimal("0"));
				
				
				BigDecimal origin = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity()));
				BigDecimal realPrice = origin.subtract(orderItemEntity.getPromotionAmount()).subtract(orderItemEntity.getCouponAmount()).subtract(orderItemEntity.getIntegrationAmount());
				orderItemEntity.setRealAmount(realPrice);
				
				
				return orderItemEntity;
			}).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}
	
	
}