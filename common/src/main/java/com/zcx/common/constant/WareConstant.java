package com.zcx.common.constant;

public class WareConstant
{

	public enum PurchaseStatus
	{
		CREATED(0,"新建"),
		ASSIGNED(1,"已分配"),
		RECEIVE(2,"已领取"),
		FINISH(3,"已完成"),
		ERROR(4,"有异常")
		;
		private Integer code;
		private String msg;


		PurchaseStatus(Integer code, String msg)
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

	public enum PurchaseDetail
	{
		CREATED(0,"新建"),
		ASSIGNED(1,"已分配"),
		BUYING(2,"正在采购"),
		FINISH(3,"已完成"),
		ERROR(4,"采购失败")
		;
		private Integer code;
		private String msg;


		PurchaseDetail(Integer code, String msg)
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
