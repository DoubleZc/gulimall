package com.zcx.gulimall.ware.listener;


import com.rabbitmq.client.Channel;
import com.zcx.common.constant.WareConstant;
import com.zcx.common.to.mq.OrderTo;
import com.zcx.common.to.mq.StockLockTo;
import com.zcx.gulimall.ware.service.WareSkuService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RabbitListener(queues = {WareConstant.WareMQ.RELEASE_QUEUE})
public class WareListener
{

	@Autowired
	WareSkuService wareSkuService;
	
	@RabbitHandler
	public void StockUnLock(Message message, Channel channel, StockLockTo to) throws IOException
	{
		try {
			wareSkuService.unLock(to);
		} catch (Exception e) {
			channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
			return;
		}
		channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
		
	}
	
	
	@RabbitHandler
	public void OrderUnLock(Message message, Channel channel, OrderTo to) throws IOException
	{
		try {
			wareSkuService.unLock(to);
		} catch (Exception e) {
			channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
			return;
		}
		channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
	}
	
	
	
	

}
