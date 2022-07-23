package com.zcx.gulimall.product.dao;

import com.zcx.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcx.gulimall.product.vo.SkuItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-20 18:00:34
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

	List<SkuItemVo.SkuSaleAttr> attrRSkuId(@Param("spuId") Long spuId);

	List<SkuItemVo.attrRSkuId> skuIds(@Param("values") List<String> values);
}
