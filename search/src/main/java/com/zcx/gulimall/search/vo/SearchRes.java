package com.zcx.gulimall.search.vo;


import com.zcx.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


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
	private  List<Integer>pag;

	List<Brand> brands;
	List<Attr>attrs;
	List<Catalog>catalogs;
	List<Nav>navs;
	List<Long> attrIds=new ArrayList<>();
	@Data
	public static  class Nav
	{
		String navName;
		String navValue;
		String link;
	}




	@Data
	public static  class Brand
	{
		Long brandId;
		String brandName;
		String brandImg;

	}

	@Override
	public String toString()
	{
		return "SearchRes{" +
				"products=" + products +
				", pageNum=" + pageNum +
				", total=" + total +
				", totalPages=" + totalPages +
				", brands=" + brands +
				", attrs=" + attrs +
				", catalogs=" + catalogs +
				'}';
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
