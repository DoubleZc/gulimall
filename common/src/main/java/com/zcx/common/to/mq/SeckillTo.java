package com.zcx.common.to.mq;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeckillTo
{
	/***
	 * 订单号
	 */
	private String orderSn;
	/**
	 * 场次id
	 */
	private Long promotionSessionId;
	/**
	 * 商品id
	 */
	private Long skuId;
	/**
	 * 秒杀价格
	 */
	private BigDecimal seckillPrice;
	/**
	 * 购买数量
	 */
	private Integer num;
	/**
	 * 用户id
	 */
	private Long memberId;
	
}
