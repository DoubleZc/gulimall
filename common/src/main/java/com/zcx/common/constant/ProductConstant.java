package com.zcx.common.constant;

public class ProductConstant
{
	public enum AttrType
	{
		BASE(1,"base"),
		SALE(0,"sale");


		private final Integer code;
		private final String msg;


		AttrType(Integer code, String msg)
		{
			this.code = code;
			this.msg = msg;
		}

		public Integer getCode()
		{
			return code;
		}

		public String getMsg()
		{
			return msg;
		}
	}


}
