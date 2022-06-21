package com.zcx.gulimall.product.dao;

import com.zcx.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-20 18:00:35
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
