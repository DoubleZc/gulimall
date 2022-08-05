package com.zcx.gulimall.ware.vo;

import lombok.Data;

import java.util.List;


@Data
public class WareSkuLockVo
{
	private String orderSn;
	
	List<OrderItemVo> itemVo;


}
