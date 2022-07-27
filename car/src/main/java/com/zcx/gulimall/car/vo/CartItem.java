package com.zcx.gulimall.car.vo;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartItem
{
	
	
	private Long skuId;
	private boolean check=true;
	private String title;
	private String image;
	private List<String> skuAttr;
	private BigDecimal price;
	private Integer count;
	private BigDecimal totalPrice;
	
	
	public BigDecimal getTotalPrice()
	{
		if (count!=null)
		{
			return price.multiply(BigDecimal.valueOf(count));
		}
		return BigDecimal.valueOf(0);
	}
}
