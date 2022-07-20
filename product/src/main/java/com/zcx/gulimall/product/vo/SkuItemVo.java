package com.zcx.gulimall.product.vo;

import com.zcx.gulimall.product.entity.SkuImagesEntity;
import com.zcx.gulimall.product.entity.SkuInfoEntity;
import com.zcx.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
public class SkuItemVo
{
	SkuInfoEntity info;
	List<SkuImagesEntity> images;
	SpuInfoDescEntity desc;

	List<SkuSaleAttr> skuSaleAttrs;

	List<SpuItemBaseAttr>groupAttrs;

	Map<Long,List<SkuSaleAttr>> spuAttrs;




	@Data
	public  static class SkuSaleAttr
	{
		private Long attrId;
		private String attrName;
		private String attrValue;
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
