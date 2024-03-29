package com.zcx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.common.utils.PageUtils;
import com.zcx.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.zcx.gulimall.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-20 18:00:34
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

	List<SkuItemVo.SkuSaleAttr> listBySkuId(List<SkuItemVo.SkuSaleAttr> attrRSkuId, Long skuId);

	Map<String, Long> MapBySpuId(List<Long> collect);

	List<SkuItemVo.SkuSaleAttr> attrRSkuId(Long spuId);
	
	List<String> getAttrList(Long skuId);
	
	Map<Long, List<String>> getAttrMap(List<Long> skuIds);
}

