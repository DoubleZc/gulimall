package com.zcx.gulimall.order.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
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
				if (cause!=null)
				{
					log.error("消息未送到交换机：{}",cause);
				}
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
				
				log.error("消息未送到队列, 交换机：{},路由键：{},消息：{}",returned.getExchange(),returned.getRoutingKey(),returned.getMessage().toString());
			}
		});
		
	}
	
	
	
	
	
	
}
