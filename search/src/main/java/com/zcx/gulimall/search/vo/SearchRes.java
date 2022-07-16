package com.zcx.gulimall.search.vo;


import com.zcx.common.to.es.SkuEsModel;
import lombok.Data;
import java.util.List;


@Data

public class SearchRes
{

	//查询到的所有商品信息
	private List<SkuEsModel> products;

	/*
	分页信息
	 */
	private Integer pageNum;//当前页
	private Long total;//总数
	private Integer totalPages;//总页码

	List<Brand> brands;
	List<Attr>attrs;
	List<Catalog>catalogs;

	@Data
	public static  class Brand
	{
		Long brandId;
		String brandName;
		String brandImg;

	}

	@Data
	public static  class Attr
	{
		private Long attrId;
		private String attrName;
		private List<String> attrValue;

	}

	@Data
	public static  class Catalog
	{
		private Long catalogId;
		private String catalogName;

	}


}
