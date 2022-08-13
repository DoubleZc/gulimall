package com.zcx.gulimall.order.config;


import com.zcx.common.constant.ComConstant;
import com.zcx.common.constant.OrderConstant;
import com.zcx.common.constant.WareConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Configuration
@ComponentScan(basePackages = {"com.zcx.common.config"})
public class MyRabbitConfig
{
	
	
	@Bean
	public MessageConverter messageConverter()
	{
		return new Jackson2JsonMessageConverter();
	}
	
	
	@Bean
	public Queue orderDelayQueue()
	{
		Map<String, Object> arguments = new HashMap<>();
		arguments.put(ComConstant.X_DEAD_EXCHANGE, OrderConstant.OrderMQ.EXCHANGE);
		arguments.put(ComConstant.X_DEAD_ROUTEKEY, OrderConstant.RouteKey.TO_RELEASE_QUEUE.key);
		arguments.put(ComConstant.X_MESSAGE_TTL, 60000);
		return new Queue(OrderConstant.OrderMQ.DELAY_QUEUE, true, false, false, arguments);
	}
	
	@Bean
	public Queue orderReleaseQueue()
	{
		return new Queue(OrderConstant.OrderMQ.RELEASE_QUEUE, true, false, false);
	}
	
	
	
	@Bean
	public Queue orderSeckillQueue()
	{
		return new Queue(OrderConstant.OrderMQ.SECKILL_QUEUE, true, false, false);
	}
	
	
	@Bean
	public Exchange orderEventExchange()
	{
		return new TopicExchange(OrderConstant.OrderMQ.EXCHANGE, true, false);
	}
	
	
	@Bean
	public Binding orderCreateOrderBinding()
	{
		return new Binding(OrderConstant.OrderMQ.DELAY_QUEUE,
				Binding.DestinationType.QUEUE,
				OrderConstant.OrderMQ.EXCHANGE,
				OrderConstant.RouteKey.TO_DELAY_QUEUE.key, null);
	}
	
	
	@Bean
	public Binding orderReleaseOrderBinding()
	{
		return new Binding(OrderConstant.OrderMQ.RELEASE_QUEUE,
				Binding.DestinationType.QUEUE,
				OrderConstant.OrderMQ.EXCHANGE,
				OrderConstant.RouteKey.TO_RELEASE_QUEUE.key, null);
	}
	
	
	@Bean
	public Binding orderReleaseWareBinding()
	{
		return new Binding(WareConstant.WareMQ.RELEASE_QUEUE,
				Binding.DestinationType.QUEUE,
				OrderConstant.OrderMQ.EXCHANGE,
				OrderConstant.RouteKey.TO_WARE_RELEASE_QUEUE.key,
				null);
	}
	
	
	
	@Bean
	public Binding orderSeckillBinding()
	{
		return new Binding(OrderConstant.OrderMQ.SECKILL_QUEUE,
				Binding.DestinationType.QUEUE,
				OrderConstant.OrderMQ.EXCHANGE,
				OrderConstant.RouteKey.TO_SECKILL_QUEUE.key, null);
	}
	

}
