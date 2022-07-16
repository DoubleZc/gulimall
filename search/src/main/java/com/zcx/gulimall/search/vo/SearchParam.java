package com.zcx.gulimall.search.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchParam
{
	String keyword;//搜索关键字
	Long catalog3Id;//三级分类id

	String sort;//排序条件
	Integer hasStock;//是否有库存
	String skuPrice;//价格区间
	List<Long> brandId;//品牌id
	List<String> attrs;//属性筛选
	Integer pageNum=1;




}
