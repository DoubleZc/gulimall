package com.zcx.gulimall.ware.dao;

import com.zcx.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.zcx.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 * 
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-21 18:17:14
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
	Integer lockStock(@Param("skuId") Long skuId, @Param("count") Integer count, @Param("wareId") Long wareId);
	
	Integer updateUnlock(@Param("to") WareOrderTaskDetailEntity to);
}
