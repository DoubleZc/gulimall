package com.zcx.gulimall.mqsave.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.common.utils.R;
import com.zcx.gulimall.mqsave.constant.MqStatus;
import com.zcx.gulimall.mqsave.pojo.MqEntity;


public interface MqService extends IService<MqEntity>
{
	
	R sendAndSave(MqEntity entity);
	
	void updateStatus(MqStatus status, String id);
	
	void successReceive(String id);
	
	com.zcx.common.utils.R send(MqEntity entity);
}
