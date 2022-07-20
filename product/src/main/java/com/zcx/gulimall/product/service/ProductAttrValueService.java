package com.zcx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.common.utils.PageUtils;
import com.zcx.gulimall.product.entity.ProductAttrValueEntity;
import com.zcx.gulimall.product.vo.BaseAttrs;
import com.zcx.gulimall.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-20 18:00:34
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);
	void saveRelation(Long id, List<BaseAttrs> baseAttrs);

	List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId);

	void updateSpu(Long spuId,List<ProductAttrValueEntity> productAttrValueEntity);

	List<SkuItemVo.SpuItemBaseAttr> listBySpuId(Long spuId);
}

