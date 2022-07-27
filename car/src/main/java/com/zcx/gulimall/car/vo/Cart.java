package com.zcx.gulimall.car.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class Cart
{
	List<CartItem>items;
	private Integer countNum;
	private Integer getCountType;
	private BigDecimal totalAmount;
	private BigDecimal reduce=new BigDecimal("0");
	
	
	public Integer getCountNum()
	{
		Integer count =0;
		if (items!=null&&!items.isEmpty())
		{
			for (CartItem item : items) {
				count += item.getCount();
			}
		}
		return count;
	}
	
	
	public Integer getGetCountType()
	{
		if (items!=null&&!items.isEmpty())
		{
			return items.size();
		}
		return 0;
	}
	
	public BigDecimal getTotalAmount()
	{
		
		BigDecimal count = BigDecimal.valueOf(0);
		if (items!=null&&!items.isEmpty())
		{
			for (CartItem item : items) {
			count =count.add(item.getTotalPrice());
			}
			count= count.subtract(reduce);
		}
		return count;
	}
	
	
	
}


