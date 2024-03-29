package com.zcx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.common.utils.PageUtils;
import com.zcx.gulimall.product.entity.SkuInfoEntity;
import com.zcx.gulimall.product.entity.SpuInfoEntity;
import com.zcx.gulimall.product.vo.SkuItemVo;
import com.zcx.gulimall.product.vo.SpuSaveVo;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-20 18:00:34
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);


	PageUtils queryPageByCondition(Map<String, Object> params);

	List<SkuInfoEntity> getBySpuId(Long spuId);

	SkuItemVo item(Long skuId);
	
	
	List<SkuInfoEntity> getByIds(List<Long> skuIds);
	
	
	SpuInfoEntity getSpuInfo(Long skuId);
}

