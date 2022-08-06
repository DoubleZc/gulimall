package com.zcx.gulimall.mqsave.config;

import com.zcx.gulimall.mqsave.constant.MqStatus;
import com.zcx.gulimall.mqsave.service.MqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;



@Configuration
public class RabbitConfig
{
	
	@Bean
	public MessageConverter messageConverter(){
		return new Jackson2JsonMessageConverter();
	}
	
	
	
	
}
