package com.zcx.gulimall.order.vo;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderConfirmVo
{
	List<MemberAddressVo> addressVoList;
	
	//所有选中的购物项
	List<OrderItemVo>items;
	
	
	//优惠信息
	Integer integration;
	
	BigDecimal total;
	
	BigDecimal payPrice;
	
	
	//防重令牌
	String orderToken;
	
	Integer count;
	
	
	public Integer getCount()
	{
		Integer count = 0;
		if (items==null)
		{
			return count;
		}
		for (OrderItemVo item : items) {
			count += item.getCount();
			
		}
		return count;
	}
	
	
	
	public BigDecimal getTotal()
	{
		if (items==null)
		{
			return BigDecimal.valueOf(0);
		}
		BigDecimal price = new BigDecimal("0");
		for (OrderItemVo item : items) {
			Integer count = item.getCount();
			price = price.add(item.getPrice().multiply(BigDecimal.valueOf(count)));
		}
		return price;
	}
	
	public BigDecimal getPayPrice()
	{
		return getTotal();
	}
}
