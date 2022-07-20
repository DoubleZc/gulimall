package com.zcx.gulimall.search.constant;

public class EsContant
{
	public static final String PRODUCT_INDEX="product";
	public static final Integer PRODUCT_PAGESIZA=2;



	 public static enum TermsType
	{
		STRING("string"),
		LONG("long")
		;

		String type;


		TermsType(String type){
			this.type=type;
		}


		public String getType()
		{
			return type;
		}
	}

}
