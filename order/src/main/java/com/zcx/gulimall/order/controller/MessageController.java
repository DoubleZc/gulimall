package com.zcx.gulimall.order.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
public class MessageController
{
	
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	@GetMapping("/send")
	public String send(@RequestParam(value = "num",defaultValue = "10")int num)
	{
		for (int i = 0; i < num; i++) {
			
			String s=String.valueOf(i);
			rabbitTemplate.convertAndSend("hello.java.exchange","hello.java",s,new CorrelationData(UUID.randomUUID().toString()));
			log.info("消息发送完成{}",s);
		}
		return "";
	}
	
}
