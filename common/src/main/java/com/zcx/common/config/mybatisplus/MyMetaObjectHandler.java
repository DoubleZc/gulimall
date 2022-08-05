package com.zcx.common.config.mybatisplus;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;


@Component
public class MyMetaObjectHandler implements MetaObjectHandler
{
	@Override
	public void insertFill(MetaObject metaObject)
	{
		metaObject.setValue("createTime",  new Date());
		metaObject.setValue("modifyTime",new Date());
	}

	@Override
	public void updateFill(MetaObject metaObject)
	{
		metaObject.setValue("modifyTime",new Date());
	}
	
	
}
