package com.zcx.gulimall.mqsave.constant;

public enum MqStatus
{
	DEFAULTS(0),
	TO_EXCHANGE_ERROR(1),
	TO_QUEUE_ERROR(2),
	SUCCESS(3),
	RECEIVE(4),
	;
	
	private final Integer code;
	MqStatus(Integer code)
	{
		this.code=code;
		
	}
	
	public Integer getCode()
	{
		return code;
	}
}
