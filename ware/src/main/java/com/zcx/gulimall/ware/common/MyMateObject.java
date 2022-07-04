package com.zcx.gulimall.ware.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMateObject implements MetaObjectHandler
{
	@Override
	public void insertFill(MetaObject metaObject)
	{
		metaObject.setValue("createTime",new Date());
		metaObject.setValue("updateTime",new Date());
	}

	@Override
	public void updateFill(MetaObject metaObject)
	{

		metaObject.setValue("updateTime",new Date());
	}
}
