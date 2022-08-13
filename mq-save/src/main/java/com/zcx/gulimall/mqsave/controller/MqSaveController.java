package com.zcx.gulimall.mqsave.controller;


import com.zcx.common.utils.R;
import com.zcx.gulimall.mqsave.constant.MqStatus;
import com.zcx.gulimall.mqsave.pojo.MqEntity;
import com.zcx.gulimall.mqsave.service.MqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class MqSaveController
{
	
	@Autowired
	MqService mqService;
	
	@RequestMapping("/send")
	public R sendMessage(@RequestBody MqEntity entity)
	{
		log.info("sendMessage:{}",entity.getRouteKey());
		return mqService.sendAndSave(entity);
	}
	
	
	@RequestMapping("/success")
	public R success(@RequestBody String id)
	{
		 mqService.successReceive(id);
		 log.info("消息{}:成功消费",id);
		 return R.ok();
	}
	
	
	
}
