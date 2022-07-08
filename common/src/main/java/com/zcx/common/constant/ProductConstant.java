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
	public  enum UpStatus
	{
		NEW_SPU(0,"新建"),
		SPU_UP(1,"商品上架"),
		SPU_DOWN(2,"商品下架")


		;
		Integer Status;
		String msg;

		UpStatus(Integer status, String msg)
		{
			Status = status;
			this.msg = msg;
		}

		public Integer getStatus()
		{
			return Status;
		}

		public String getMsg()
		{
			return msg;
		}
	}



}
