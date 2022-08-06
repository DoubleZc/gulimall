package com.zcx.gulimall.mqsave.config;


import com.zcx.gulimall.mqsave.constant.MqStatus;
import com.zcx.gulimall.mqsave.service.MqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class InitRabbitTemplate
{
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	@Autowired
	MqService mqService;
	
	
	@PostConstruct
	public void initRabbit()
	{
		rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback()
		{
			@Override
			public void confirm(CorrelationData correlationData, boolean ack, String cause)
			{
				if (!ack)
				{
					log.warn("消息发送未到Exchange");
					mqService.updateStatus(MqStatus.TO_EXCHANGE_ERROR,correlationData.getId());
				}else
				{
					log.info("消息已送达Exchange");
					mqService.updateStatus(MqStatus.SUCCESS,correlationData.getId());
				}
			}
		});
		
		
		
		
		rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback()
		{
			@Override
			public void returnedMessage(ReturnedMessage returned)
			{
				log.warn("消息未送达Queue");
				mqService.updateStatus(MqStatus.TO_QUEUE_ERROR,returned.getMessage().getMessageProperties().getMessageId());
			}
		});
		
		
		
	}
	
}
