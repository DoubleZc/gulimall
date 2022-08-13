package com.zcx.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.comm.CosException;
import com.zcx.common.constant.OrderConstant;
import com.zcx.common.to.mq.SeckillTo;
import com.zcx.gulimall.order.vo.AlipayParam;
import com.zcx.common.to.MemberTo;
import com.zcx.common.to.mq.MqTo;
import com.zcx.common.to.mq.OrderTo;
import com.zcx.common.utils.*;
import com.zcx.gulimall.order.dao.OrderDao;
import com.zcx.gulimall.order.entity.OrderEntity;
import com.zcx.gulimall.order.entity.OrderItemEntity;
import com.zcx.gulimall.order.entity.PaymentInfoEntity;
import com.zcx.gulimall.order.feign.*;
import com.zcx.gulimall.order.service.OrderItemService;
import com.zcx.gulimall.order.service.OrderService;
import com.zcx.gulimall.order.service.PaymentInfoService;
import com.zcx.gulimall.order.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("orderService")
@Slf4j
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
	
	@Autowired
	MqFeignService mqFeignService;
	
	@Autowired
	PaymentInfoService paymentInfoService;
	
	
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
					//订单创建成功
					r.put("orderSn",orderCreatTo.getOrderEntity().getOrderSn());
					r.put("price",payAmount);
					
					OrderTo orderTo = new OrderTo();
					BeanUtils.copyProperties(orderCreatTo.getOrderEntity(),orderTo);
					
					try {
						mqFeignService.sendMessage(new MqTo(OrderConstant.OrderMQ.EXCHANGE,OrderConstant.RouteKey.TO_DELAY_QUEUE.key,orderTo));
						log.info("订单创建成功,发送消息到MQ");
					} catch (Exception e) {
						e.printStackTrace();
					}
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
	
	
	
	
	@Override
	public boolean closeOrder(OrderEntity orderEntity)
	{
		try {
			Long id = orderEntity.getId();
			OrderEntity now = getById(id);
			Integer status = now.getStatus();
			if (status.equals(OrderConstant.OrderStatusEnum.CREATE_NEW.getCode()))
			{
				OrderEntity change = new OrderEntity();
				change.setId(now.getId());
				change.setStatus(OrderConstant.OrderStatusEnum.CANCLED.getCode());
				updateById(change);
				
				
				OrderTo orderTo = new OrderTo();
				BeanUtils.copyProperties(orderEntity,orderTo);
				
				try {
					mqFeignService.sendMessage(new MqTo(OrderConstant.OrderMQ.EXCHANGE, OrderConstant.RouteKey.TO_WARE_RELEASE_QUEUE.key, orderTo));
					log.info("关闭订单,解锁库存消息到MQ");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}
			return true;
		} catch (Exception e) {
			return  false;
		}
	}
	
	@Override
	public PayVo getOrderPay(String orderSn)
	{
		
		PayVo payVo = new PayVo();
		OrderEntity one = getByOrderSn(orderSn);
		
		//订单号
		payVo.setOut_trade_no(orderSn);
		//付款金额
		BigDecimal bigDecimal = one.getPayAmount().setScale(2, RoundingMode.UP);
		payVo.setTotal_amount(bigDecimal.toString());
		// 订单名称
		payVo.setSubject("谷粒商城");
		//商品描述
		
		
		return payVo;
	}
	
	@Override
	public OrderEntity getByOrderSn(String orderSn)
	{
		return getOne(new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getOrderSn, orderSn));
	}
	
	@Override
	public PageUtils listItem(Map<String, Object> params)
	{
		MemberTo msg = MyThreadMsg.getMsg(MemberTo.class);
		
		IPage<OrderEntity> page = this.page(
				new Query<OrderEntity>().getPage(params),
				new QueryWrapper<OrderEntity>().eq("member_id",msg.getId()).orderByDesc("order_sn")
		);
		page.getRecords().forEach(i->{
			List<OrderItemEntity> list = orderItemService.list(new LambdaQueryWrapper<OrderItemEntity>()
					.eq(OrderItemEntity::getOrderSn, i.getOrderSn()));
			i.setItems(list);
		});
		
		
		return new PageUtils(page);
	
	
	}
	
	@Override
	public String handlePayResult(AlipayParam param)
	{
		
		PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
		paymentInfoEntity.setAlipayTradeNo(param.getTrade_no());
		paymentInfoEntity.setOrderSn(param.getOut_trade_no());
		paymentInfoEntity.setPaymentStatus(param.getTrade_status());
		paymentInfoEntity.setCallbackTime(param.getNotify_time());
		paymentInfoService.save(paymentInfoEntity);
		
		if (param.getTrade_status().equals("TRADE_SUCCESS")||param.getTrade_status().equals("TRADE_FINISHED"))
		{
			String orderSn = param.getOut_trade_no();
			OrderEntity orderEntity = new OrderEntity();
			orderEntity.setStatus(OrderConstant.OrderStatusEnum.PAYED.getCode());
			update(orderEntity,new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getOrderSn,orderSn));
		}
		return "success";
		
	}
	
	@Override
	public String getAlipaySn(String orderSn)
	{
		PaymentInfoEntity one = paymentInfoService.getOne(new LambdaQueryWrapper<PaymentInfoEntity>().eq(PaymentInfoEntity::getOrderSn, orderSn));
		return one.getAlipayTradeNo();
		
	}
	
	@Override
	public void createSeckillOrder(SeckillTo to)
	{
		OrderEntity orderEntity = new OrderEntity();
		
		orderEntity.setOrderSn(to.getOrderSn());
		orderEntity.setMemberId(to.getMemberId());
		orderEntity.setPayAmount(to.getSeckillPrice().multiply(BigDecimal.valueOf(to.getNum())));
		orderEntity.setStatus(OrderConstant.OrderStatusEnum.CREATE_NEW.getCode());
		save(orderEntity);
		
		
		OrderItemEntity orderItemEntity = new OrderItemEntity();
		orderItemEntity.setOrderSn(to.getOrderSn());
		orderItemEntity.setSkuId(to.getSkuId());
		orderItemEntity.setSkuQuantity(to.getNum());
		orderItemService.save(orderItemEntity);
		
		
		
		
		
		
		
		
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