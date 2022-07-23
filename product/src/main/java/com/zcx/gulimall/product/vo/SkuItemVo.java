package com.zcx.gulimall.product.vo;

import com.zcx.gulimall.product.entity.SkuImagesEntity;
import com.zcx.gulimall.product.entity.SkuInfoEntity;
import com.zcx.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Data
public class SkuItemVo
{
	SkuInfoEntity info;
	List<SkuImagesEntity> images;
	SpuInfoDescEntity desc;
	List<SpuItemBaseAttr>groupAttrs;
	boolean hasStock=true;
	List<SkuSaleAttr> skuSaleAttrs;




	@Data
	public  static class SkuSaleAttr
	{
		private Long attrId;
		private String attrName;
		private String attrValue;
		private String attrValues;
		List<attrRSkuId> attrRSkuIds;

	}



	@Data
	public  static class attrRSkuId
	{
		private String name;
		private String skuIds;
	}



	@Data
	public static class SpuItemBaseAttr
	{
		String groupName;
		List<BaseAttrs>attrs;
	}

	@Data
	public static class  BaseAttrs
	{
		private String attrName;
		private String attrValue;
	}




}
