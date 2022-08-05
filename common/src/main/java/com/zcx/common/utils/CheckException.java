package com.zcx.common.utils;





public class CheckException extends Exception
{
	ExceptionCode code;
	public CheckException(ExceptionCode code)
	{
		this.code =code;
	}

	public ExceptionCode getCode()
	{
		return code;
	}

}
