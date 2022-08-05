package com.zcx.gulimall.order.config;


import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.zcx.common.constant.ComConstant;
import com.zcx.common.constant.OrderConstant;
import com.zcx.gulimall.order.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Configuration
@ComponentScan(basePackages={"com.zcx.common.config"})
public class MyRabbitConfig
{
	
	@RabbitListener(queues = {OrderConstant.OrderMQ.RELEASE_QUEUE})
	public void listener(OrderEntity entity, Channel channel,Message message){
		try {
			channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.warn(" 超时：订单时间：{},现在时间：{}，订单号{}",entity.getModifyTime(),new Date(),entity.getOrderSn());;
	}
	
	
	@Bean
	public MessageConverter messageConverter(){
	return new Jackson2JsonMessageConverter();
	}
	
	
	@Bean
	public Queue orderDelayQueue()
	{
		Map<String ,Object> arguments=new HashMap<>();
		arguments.put(ComConstant.X_DEAD_EXCHANGE,OrderConstant.OrderMQ.EXCHANGE);
		arguments.put(ComConstant.X_DEAD_ROUTEKEY,OrderConstant.RouteKey.TO_RELEASE_QUEUE.key);
		arguments.put(ComConstant.X_MESSAGE_TTL,20000);
		return new Queue(OrderConstant.OrderMQ.DELAY_QUEUE, true, false, false,arguments);
	}
	
	@Bean
	public Queue orderReleaseQueue()
	{
		return new Queue(OrderConstant.OrderMQ.RELEASE_QUEUE, true, false, false);
	}
	
	
	@Bean
	public Exchange orderEventExchange(){
		return new TopicExchange(OrderConstant.OrderMQ.EXCHANGE,true,false);
	}
	
	
	@Bean
	 public Binding orderCreateOrderBinding(){
		return new Binding(OrderConstant.OrderMQ.DELAY_QUEUE,
				Binding.DestinationType.QUEUE,
				OrderConstant.OrderMQ.EXCHANGE,
				OrderConstant.RouteKey.TO_DELAY_QUEUE.key, null);
	}
	
	
	
	@Bean
	public Binding orderReleaseOrderBinding(){
		return new Binding(OrderConstant.OrderMQ.RELEASE_QUEUE,
				Binding.DestinationType.QUEUE,
				OrderConstant.OrderMQ.EXCHANGE,
				OrderConstant.RouteKey.TO_RELEASE_QUEUE.key, null);
	}
	
	
	
	
}
