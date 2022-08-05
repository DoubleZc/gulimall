package com.zcx.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.common.comm.CosException;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.R;
import com.zcx.gulimall.order.entity.OrderEntity;
import com.zcx.gulimall.order.vo.OrderConfirmVo;
import com.zcx.gulimall.order.vo.OrderSubmitVo;

import java.util.Map;

/**
 * 订单
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-21 17:50:38
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
	
	OrderConfirmVo confirm();
	
	R submitOrder(OrderSubmitVo vo) throws CosException.NotStock;
}

