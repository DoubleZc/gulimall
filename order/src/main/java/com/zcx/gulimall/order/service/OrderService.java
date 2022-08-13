package com.zcx.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.common.comm.CosException;
import com.zcx.common.to.mq.SeckillTo;
import com.zcx.gulimall.order.vo.AlipayParam;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.R;
import com.zcx.gulimall.order.entity.OrderEntity;
import com.zcx.gulimall.order.vo.OrderConfirmVo;
import com.zcx.gulimall.order.vo.OrderSubmitVo;
import com.zcx.gulimall.order.vo.PayVo;

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
	
	boolean closeOrder(OrderEntity orderEntity);
	
	PayVo getOrderPay(String orderSn);
	
	OrderEntity getByOrderSn(String orderSn);
	
	PageUtils listItem(Map<String, Object> params);
	
	String handlePayResult(AlipayParam param);
	
	String getAlipaySn(String orderSn);
	
	void createSeckillOrder(SeckillTo to);
}

