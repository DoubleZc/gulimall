package com.zcx.gulimall.order.dao;

import com.zcx.gulimall.order.entity.OrderSettingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 * 
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-21 17:50:38
 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {
	
}
