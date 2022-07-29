package com.zcx.gulimall.order.config;


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

@Configuration
@ComponentScan(basePackages={"com.zcx.common.config"})
public class MyRabbitConfig
{
	
	
	@Bean
	public MessageConverter messageConverter(){

	return new Jackson2JsonMessageConverter();
	}
	
	
}
