package com.zcx.gulimall.member.config;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class MyMetaObjectHandler implements MetaObjectHandler
{
	@Override
	public void insertFill(MetaObject metaObject)
	{
		metaObject.setValue("createTime", new Date());

	}

	@Override
	public void updateFill(MetaObject metaObject)
	{

	}
}
