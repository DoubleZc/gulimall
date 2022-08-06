package com.zcx.gulimall.mqsave.controller;


import com.zcx.common.utils.R;
import com.zcx.gulimall.mqsave.pojo.MqEntity;
import com.zcx.gulimall.mqsave.service.MqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MqSaveController
{
	
	@Autowired
	MqService mqService;
	
	@RequestMapping("/send")
	public R sendMessage(@RequestBody MqEntity entity)
	{
		return mqService.sendAndSave(entity);
	}
	
}
