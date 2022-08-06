package com.zcx.gulimall.mqsave.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.utils.ExceptionCode;
import com.zcx.common.utils.R;
import com.zcx.gulimall.mqsave.constant.MqStatus;
import com.zcx.gulimall.mqsave.mapper.MqMapper;
import com.zcx.gulimall.mqsave.pojo.MqEntity;
import com.zcx.gulimall.mqsave.service.MqService;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqServiceImpl extends ServiceImpl<MqMapper, MqEntity> implements MqService
{
	
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	
	@Override
	public R sendAndSave(MqEntity entity)
	{
		entity.setMessageStatus(MqStatus.DEFAULTS.getCode());
		save(entity);
		R send=null;
		int time=0;
		while (time<10){
			time++;
			 send = send(entity);
			if (!ExceptionCode.MQ_ERROR.getCode().equals(send.getCode()))
			{
				break;
			}
		}
		if (time==10)
		{
			log.warn("重发10次未成功:"+entity.getMessageId().toString());
			updateStatus(MqStatus.TO_EXCHANGE_ERROR,entity.getMessageId().toString());
		}
		return send;
	}
	
	@Override
	public void updateStatus(MqStatus status, String id)
	{
		MqEntity mqEntity = new MqEntity();
		mqEntity.setMessageStatus(status.getCode());
		update(mqEntity,new LambdaQueryWrapper<MqEntity>().eq(MqEntity::getMessageId,Long.valueOf(id)).and(
				mqEntityLambdaQueryWrapper ->mqEntityLambdaQueryWrapper.eq(MqEntity::getMessageStatus,MqStatus.DEFAULTS.getCode()) ));
	}
	
	@Override
	public R send(MqEntity entity)
	{
		try {
			entity.setMessageStatus(MqStatus.DEFAULTS.getCode());
			String[] s = entity.getClassType().split(" ");
			Class<?> aClass = Class.forName(s[1]);
			// 消息发送，带MessageId
			MessagePostProcessor messagePostProcessor = msg -> {
				MessageProperties messageProperties = msg.getMessageProperties();
				// 消息id
				messageProperties.setMessageId(entity.getMessageId().toString());
				return msg;
			};
			
			rabbitTemplate.convertAndSend(entity.getToExchange(), entity.getRouteKey(), JSON.parseObject(entity.getContent(), aClass),messagePostProcessor,new CorrelationData(entity.getMessageId().toString()));
		} catch (AmqpException e) {
			//发送消息错误
			log.warn("消息发送异常"+entity.getMessageId());
			return R.error(ExceptionCode.MQ_ERROR);
		} catch (ClassNotFoundException e) {
			//转换类型错误
			log.warn("类型转换异常"+entity.getMessageId());
			return R.error(ExceptionCode.CLASS_ERROR);
		}
		return R.ok();
	}
}
