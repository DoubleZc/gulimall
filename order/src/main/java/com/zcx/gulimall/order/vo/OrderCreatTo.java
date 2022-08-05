package com.zcx.gulimall.order.vo;


import com.zcx.gulimall.order.entity.OrderEntity;
import com.zcx.gulimall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreatTo
{
	private OrderEntity orderEntity;
	private List<OrderItemEntity> items;
	private BigDecimal payPrice;
	private BigDecimal fare;
	
	
	
}
