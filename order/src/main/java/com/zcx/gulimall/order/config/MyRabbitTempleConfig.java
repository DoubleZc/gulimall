package com.zcx.gulimall.order.config;


import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class MyRabbitTempleConfig
{
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	
	@PostConstruct
	public void initRabbitTemplate()
	{
		rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback()
		{
			/***
			 *
			 * @param correlationData  当前消息的唯一关联数据
			 * @param ack  消息是否成功收到 只要消息发送到就返回true
			 * @param cause 失败原因
			 */
			@Override
			public void confirm(CorrelationData correlationData, boolean ack, String cause)
			{
				System.out.println(correlationData + "_" + ack + "_" + cause);
			}
		});
		
		
		rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback()
		{
			/***只要消息没有投递给指定对列回调
			 * Message message; 投递失败详细内容
			 * int replyCode;    回复状态码
			 * String replyText; 回复内容
			 * String exchange;   当时这个消息发给哪个交换机
			 * String routingKey; 当时这个消息发给哪个路由键
			 * @param returned
			 */
			@Override
			public void returnedMessage(ReturnedMessage returned)
			{
				System.out.println(returned);
			}
		});
		
	}
	
	
	
	
	
	
}
