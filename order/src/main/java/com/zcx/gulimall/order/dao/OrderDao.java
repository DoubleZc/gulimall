package com.zcx.gulimall.order.dao;

import com.zcx.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-21 17:50:38
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
