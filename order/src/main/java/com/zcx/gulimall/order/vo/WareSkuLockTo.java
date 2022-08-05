package com.zcx.gulimall.order.vo;

import lombok.Data;

import java.util.List;
@Data
public class WareSkuLockTo
{
	private String orderSn;
	
	List<OrderItemVo> itemVo;


}
