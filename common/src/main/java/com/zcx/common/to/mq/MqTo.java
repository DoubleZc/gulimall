package com.zcx.common.to.mq;


import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MqTo
{
	
	private String toExchange;
	
	private String routeKey;
	
	private String content;
	
	private String classType;
	
	public MqTo(String toExchange, String routeKey, Object o)
	{
		this.toExchange = toExchange;
		this.routeKey = routeKey;
		this.content= JSON.toJSONString(o);
		this.classType = o.getClass().toString();
	}
}
