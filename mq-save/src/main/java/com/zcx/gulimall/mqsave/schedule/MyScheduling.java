package com.zcx.gulimall.mqsave.schedule;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.gulimall.mqsave.constant.MqStatus;
import com.zcx.gulimall.mqsave.pojo.MqEntity;
import com.zcx.gulimall.mqsave.service.MqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@EnableScheduling
@Component
@Slf4j
public class MyScheduling
{
	@Autowired
	MqService mqService;
	
	@Scheduled(cron = "0 0/30 * * * ? ")
	public void updateMq(){
		log.info("自动扫描数据库");
		List<MqEntity> list = mqService.list(new LambdaQueryWrapper<MqEntity>().ne(MqEntity::getMessageStatus,MqStatus.SUCCESS.getCode()).and(i->i.ne(
				MqEntity::getMessageStatus,MqStatus.RECEIVE.getCode()
		)));
		if (list.isEmpty())
			return;
		list.forEach(i-> mqService.send(i));
	}
}
