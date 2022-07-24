package com.zcx.common.utils;


import com.zcx.common.utils.ExceptionCode;

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
