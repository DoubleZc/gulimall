package com.zcx.gulimall.order.vo;


import com.zcx.gulimall.order.entity.OrderEntity;
import lombok.Data;

@Data
public class SubmitOrderResVo
{
	private OrderEntity order;
	private Integer code;
	
}
