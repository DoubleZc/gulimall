package com.zcx.gulimall.order.listener;


import com.rabbitmq.client.Channel;
import com.zcx.common.constant.OrderConstant;
import com.zcx.common.to.mq.OrderTo;
import com.zcx.gulimall.order.entity.OrderEntity;
import com.zcx.gulimall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
@RabbitListener(queues = {OrderConstant.OrderMQ.RELEASE_QUEUE})
@Slf4j
public class OrderListener
{
	
	@Autowired
	OrderService orderService;
	
	@RabbitHandler
	public void listener(OrderTo entity, Channel channel, Message message){
		OrderEntity orderEntity = new OrderEntity();
		BeanUtils.copyProperties(entity,orderEntity);
		
		boolean b = orderService.closeOrder(orderEntity);
		if (b){
			try {
				channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			try {
				channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
}
