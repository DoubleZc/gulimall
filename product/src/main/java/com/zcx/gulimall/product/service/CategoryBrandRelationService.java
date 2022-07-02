package com.zcx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.common.utils.PageUtils;
import com.zcx.gulimall.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-20 18:00:35
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity>
{

	PageUtils queryPage(Map<String, Object> params);

	void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

	void updateBrand(Long brandId, String name);

	void updateCategory(Long catId, String name);

	List<CategoryBrandRelationEntity> getBrandByCatId(Long catId);

}

