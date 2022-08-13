package com.zcx.gulimall.ware.config;

import com.zcx.common.constant.ComConstant;
import com.zcx.common.constant.WareConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyRabbitConfig
{
	
	
	@Bean
	public MessageConverter messageConverter(){
		return new Jackson2JsonMessageConverter();
	}
	
	
	@Bean
	public Exchange stockEventExchange(){
		return new TopicExchange(WareConstant.WareMQ.EXCHANGE,true,false);
	}
	
	@Bean
	public Queue stockDelayQueue()
	{
		
		Map<String ,Object> arguments=new HashMap<>();
		arguments.put(ComConstant.X_DEAD_EXCHANGE,WareConstant.WareMQ.EXCHANGE);
		arguments.put(ComConstant.X_DEAD_ROUTEKEY,WareConstant.RouteKey.TO_RELEASE_QUEUE.key);
		arguments.put(ComConstant.X_MESSAGE_TTL,120000);
		return new Queue(WareConstant.WareMQ.DELAY_QUEUE, true, false, false,arguments);
	}
	
	@Bean
	public Queue stockReleaseQueue()
	{
		return new Queue(WareConstant.WareMQ.RELEASE_QUEUE, true, false, false);
	}
	
	
	@Bean
	public Binding stockCreateStockBinding(){
		return new Binding(WareConstant.WareMQ.DELAY_QUEUE,
				Binding.DestinationType.QUEUE,
				WareConstant.WareMQ.EXCHANGE,
				WareConstant.RouteKey.TO_DELAY_QUEUE.key, null);
	}
	
	
	
	@Bean
	public Binding stockReleaseStockBinding(){
		return new Binding(WareConstant.WareMQ.RELEASE_QUEUE,
				Binding.DestinationType.QUEUE,
				WareConstant.WareMQ.EXCHANGE,
				WareConstant.RouteKey.TO_RELEASE_QUEUE.key, null);
	}
	
	
	
}
