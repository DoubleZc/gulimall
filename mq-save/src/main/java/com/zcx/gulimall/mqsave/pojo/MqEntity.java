package com.zcx.gulimall.mqsave.pojo;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("mq_message")
@Data
public class MqEntity
{
	@TableId
	private Long messageId;
	
	private String content;
	
	private String toExchange;
	
	private String routeKey;
	
	private String classType;
	
	private Integer messageStatus;
	@TableField(fill = FieldFill.INSERT)
	private Date createTime;
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Date updateTime;

}
