package com.zcx.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rabbitmq.client.Channel;
import com.zcx.common.comm.CosException;
import com.zcx.common.constant.OrderConstant;
import com.zcx.common.constant.WareConstant;
import com.zcx.common.to.mq.StockLockTo;
import com.zcx.common.utils.ExceptionCode;
import com.zcx.common.utils.R;
import com.zcx.gulimall.ware.entity.PurchaseDetailEntity;
import com.zcx.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.zcx.gulimall.ware.entity.WareOrderTaskEntity;
import com.zcx.gulimall.ware.entity.WareSkuEntity;
import com.zcx.gulimall.ware.feign.OrderFeignService;
import com.zcx.gulimall.ware.feign.ProductFeign;
import com.zcx.gulimall.ware.service.WareOrderTaskDetailService;
import com.zcx.gulimall.ware.service.WareOrderTaskService;
import com.zcx.gulimall.ware.vo.OrderItemVo;
import com.zcx.gulimall.ware.vo.WareSkuLockVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.Query;

import com.zcx.gulimall.ware.dao.WareSkuDao;
import com.zcx.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RabbitListener(queues = {WareConstant.WareMQ.RELEASE_QUEUE})
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService
{
	
	@Autowired
	ProductFeign productFeign;
	
	@Autowired
	WareOrderTaskService wareOrderTaskService;
	@Autowired
	WareOrderTaskDetailService wareOrderTaskDetailService;
	@Autowired
	OrderFeignService orderFeignService;
	
	
	
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	@Override
	public PageUtils queryPage(Map<String, Object> params)
	{
		IPage<WareSkuEntity> page = this.page(
				new Query<WareSkuEntity>().getPage(params),
				new QueryWrapper<WareSkuEntity>()
		);
		
		return new PageUtils(page);
	}
	
	@Override
	public PageUtils queryPageByCondition(Map<String, Object> params)
	{
		LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();
		String skuId = (String) params.get("skuId");
		String wareId = (String) params.get("wareId");
		wrapper.eq(Strings.isNotEmpty(skuId), WareSkuEntity::getSkuId, skuId);
		wrapper.eq(Strings.isNotEmpty(wareId), WareSkuEntity::getWareId, wareId);
		
		
		IPage<WareSkuEntity> page = this.page(
				new Query<WareSkuEntity>().getPage(params),
				wrapper
		);
		
		return new PageUtils(page);
		
		
	}
	
	@Override
	public void saveWareSku(List<PurchaseDetailEntity> entities)
	{
		entities.forEach(entity -> {
			Long skuId = entity.getSkuId();
			Long wareId = entity.getWareId();
			Integer skuNum = entity.getSkuNum();
			R one = productFeign.getById(skuId);
			String name = (String) one.get("name");
			LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();
			wrapper.eq(WareSkuEntity::getSkuId, skuId).eq(WareSkuEntity::getWareId, wareId);
			WareSkuEntity skuEntity = getOne(wrapper);
			if (skuEntity == null) {
				WareSkuEntity wareSkuEntity = new WareSkuEntity();
				wareSkuEntity.setSkuName(name);
				wareSkuEntity.setSkuId(skuId);
				wareSkuEntity.setWareId(wareId);
				wareSkuEntity.setStock(skuNum);
				save(wareSkuEntity);
			} else {
				Integer stock = skuEntity.getStock();
				stock += skuNum;
				Long id = skuEntity.getId();
				WareSkuEntity entity1 = new WareSkuEntity();
				entity1.setId(id);
				entity1.setStock(stock);
				updateById(entity1);
			}
		});
		
	}
	
	@Override
	public Integer getBySkuId(Long skuId)
	{
		QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("sku_id", skuId).select("sum(stock-stock_locked) as sumStock");
		WareSkuEntity one = getOne(wrapper);
		if (one != null)
			return one.getSumStock();
		else
			return 0;
	}
	
	@Override
	public Map<Long, Integer> getBySkuIds(List<Long> skuIds)
	{
		QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
		wrapper.in("sku_id", skuIds).select("sku_id,sum(stock-stock_locked) as sumStock").groupBy("sku_id");
		List<WareSkuEntity> list = list(wrapper);
		if (list != null) {
			Map<Long, Integer> collect = list.stream().collect(Collectors.toMap(WareSkuEntity::getSkuId, WareSkuEntity::getSumStock));
			System.out.println(collect);
			return collect;
		} else
			return null;
	}
	
	@Override
	@Transactional
	public R lockStock(WareSkuLockVo vo) throws CosException.NotStock
	{
		WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
		wareOrderTaskEntity.setOrderSn(vo.getOrderSn());
		
		
		
		
		List<OrderItemVo> itemVo = vo.getItemVo();
		List<Long> skuIds = itemVo.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
		Map<Long, Integer> stockMap = getBySkuIds(skuIds);
		List<OrderItemVo> list = itemVo.stream().filter(
				i -> stockMap.get(i.getSkuId()) < i.getCount()
		).collect(Collectors.toList());
		if (list.isEmpty()) {
			StockLockTo stockLockTo = new StockLockTo();
			stockLockTo.setId(wareOrderTaskEntity.getId());
			List<Long> detailIds=new ArrayList<>();
			List<WareOrderTaskDetailEntity>entities =new ArrayList<>();
			
			WareSkuDao baseMapper = this.baseMapper;
			//货源
			for (OrderItemVo i : itemVo) {
				List<WareSkuEntity> ware = list(new LambdaQueryWrapper<WareSkuEntity>().eq(WareSkuEntity::getSkuId, i.getSkuId()));
				
				boolean lockFlag=false;
				for (WareSkuEntity w : ware) {
					Integer integer = baseMapper.lockStock(i.getSkuId(), i.getCount(), w.getWareId());
					if (integer > 0) {
						//成功
						lockFlag=true;
						//发送消息给延迟队列等待回滚
						WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
						wareOrderTaskDetailEntity.setSkuId(i.getSkuId());
						wareOrderTaskDetailEntity.setSkuNum(i.getCount());
						wareOrderTaskDetailEntity.setWareId(w.getWareId());
						wareOrderTaskDetailEntity.setSkuName(i.getTitle());
						wareOrderTaskDetailEntity.setLockStatus(1);
						entities.add(wareOrderTaskDetailEntity);
						detailIds.add(wareOrderTaskDetailEntity.getId());
						break;
					}
				}
				if (!lockFlag)
				{
					throw new CosException.NotStock(ExceptionCode.ORDER_NOT_STOCK, Collections.singletonList(i.getSkuId()));
				}
			}
			
			//库存没有问题 ，库存订单提交
			wareOrderTaskService.save(wareOrderTaskEntity);
			wareOrderTaskDetailService.saveBatch(entities);
			
			stockLockTo.setDetailIds(detailIds);
			rabbitTemplate.convertAndSend(WareConstant.WareMQ.EXCHANGE,WareConstant.RouteKey.TO_DELAY_QUEUE.key,stockLockTo);
		}
		else
		{
			List<Long> collect = list.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
			throw new CosException.NotStock(ExceptionCode.ORDER_NOT_STOCK, collect);
		}
		
		return R.ok();
	}
	
	
	
	@RabbitHandler
	public void  unLock(Message message, Channel channel,StockLockTo to)
	{
		WareSkuDao baseMapper = this.baseMapper;
		List<Long> detailIds = to.getDetailIds();
		List<WareOrderTaskDetailEntity> list = wareOrderTaskDetailService.listByIds(to.getDetailIds());
		
		
		WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskService.getById(to.getId());
		String orderSn = wareOrderTaskEntity.getOrderSn();
		R status=null;
		try {
			status = orderFeignService.getStatus(orderSn);
		} catch (Exception e) {
			log.error("远程调用异常orderFeignService.getStatus");
		}
		
		Integer data = (Integer)status.get("data");
		
		if (data.equals(OrderConstant.OrderStatusEnum.CANCLED.getCode())||data==-1)
		{
			//减库存
			list.forEach(baseMapper::updateUnlock);
			//删库存订单
			wareOrderTaskDetailService.removeBatchByIds(detailIds);
			wareOrderTaskService.removeById(to.getId());
			
			try {
				channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else
		{
			
			try {
				channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		
		
	}
	
	
}