package com.zcx.gulimall.ware.listener;


import com.rabbitmq.client.Channel;
import com.zcx.common.constant.WareConstant;
import com.zcx.common.to.mq.OrderTo;
import com.zcx.common.to.mq.StockLockTo;
import com.zcx.gulimall.ware.feign.MqFeignService;
import com.zcx.gulimall.ware.service.WareSkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Component
@RabbitListener(queues = {WareConstant.WareMQ.RELEASE_QUEUE})
public class WareListener
{

	@Autowired
	WareSkuService wareSkuService;
	
	@Autowired
	MqFeignService mqFeignService;
	
	@Transactional
	@RabbitHandler
	public void StockUnLock(Message message, Channel channel, StockLockTo to) throws IOException
	{
		String messageId = message.getMessageProperties().getMessageId();
		
		try {
			wareSkuService.unLock(to);
			mqFeignService.success(messageId);
		} catch (Exception e) {
			channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
			log.info("消息{}已被退回",messageId);
			return;
		}
		log.info("消息{}已被消费",messageId);
		channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
		
	}
	
	@Transactional
	@RabbitHandler
	public void OrderUnLock(Message message, Channel channel, OrderTo to) throws IOException
	{
		String messageId = message.getMessageProperties().getMessageId();
		try {
			wareSkuService.unLock(to);
			mqFeignService.success(messageId);
		} catch (Exception e) {
			channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
			log.info("消息{}已被退回",messageId);
			return;
		}
		log.info("消息{}已被消费",messageId);
		channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
	}
	
	
	
	

}
