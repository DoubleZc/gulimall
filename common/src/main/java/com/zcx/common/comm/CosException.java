package com.zcx.common.comm;

import com.zcx.common.utils.ExceptionCode;

import java.util.List;
import java.util.Map;

public class CosException  extends Exception
{
	ExceptionCode code;
	public CosException(ExceptionCode code)
	{
		this.code=code;
	}
	public ExceptionCode getCode()
	{
		return code;
	}
	
	
	public static class CheckException extends Exception
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
	
	public static class NotStock extends RuntimeException
	{
		
		ExceptionCode code;
		List<Long> skuIds;
		
		public NotStock(ExceptionCode code,List<Long>skuIds)
		{
			this.code =code;
			this.skuIds=skuIds;
		}
		
		public ExceptionCode getCode()
		{
			return code;
		}
		
		public List<Long> getSkuIds()
		{
			return skuIds;
		}
	}
	

	
	
	
}
