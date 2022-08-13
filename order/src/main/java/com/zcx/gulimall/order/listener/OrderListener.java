package com.zcx.gulimall.order.listener;


import com.alipay.api.AlipayApiException;
import com.rabbitmq.client.Channel;
import com.zcx.common.constant.OrderConstant;
import com.zcx.common.to.mq.OrderTo;
import com.zcx.common.to.mq.SeckillTo;
import com.zcx.gulimall.order.config.AlipayTemplate;
import com.zcx.gulimall.order.entity.OrderEntity;
import com.zcx.gulimall.order.feign.MqFeignService;
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
@RabbitListener(queues = {OrderConstant.OrderMQ.RELEASE_QUEUE,OrderConstant.OrderMQ.SECKILL_QUEUE})
@Slf4j
public class OrderListener
{
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	MqFeignService mqFeignService;
	
	@Autowired
	AlipayTemplate alipayTemplate;
	
	@RabbitHandler
	public void orderCloseListener(OrderTo entity, Channel channel, Message message){
		OrderEntity orderEntity = new OrderEntity();
		BeanUtils.copyProperties(entity,orderEntity);
		boolean b = orderService.closeOrder(orderEntity);
		if (b){
			try {
				String messageId = message.getMessageProperties().getMessageId();
				alipayTemplate.close(entity.getOrderSn());
				log.info("消费 messageId:{} 关闭订单",messageId);
				mqFeignService.success(messageId);
				channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (AlipayApiException e) {
				log.warn("支付宝关单错误");
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
	
	
	@RabbitHandler
	public void orderSeckillListener(Channel channel, Message message, SeckillTo to) throws IOException
	{
		try {
			log.info("监听秒杀");
			orderService.createSeckillOrder(to);
			channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
		} catch (Exception e) {
			e.printStackTrace();
			channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
		}
	}
	
}
