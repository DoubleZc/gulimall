package com.zcx.gulimall.order.web;


import com.zcx.common.constant.OrderConstant;
import com.zcx.common.to.mq.MqTo;
import com.zcx.gulimall.order.entity.OrderEntity;
import com.zcx.gulimall.order.feign.MqFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.UUID;

@Controller
public class HelloController
{
	@Autowired
	MqFeignService mqFeignService;
	

	@GetMapping("/{page}.html")
	public String page(@PathVariable("page") String page)
	{
		
		return page;
	}
	
	
	@ResponseBody
	@GetMapping("/test")
	public String createOrder()
	{

		
		OrderEntity orderEntity=new OrderEntity();
		orderEntity.setOrderSn(UUID.randomUUID().toString());
		orderEntity.setModifyTime(new Date());
		mqFeignService.sendMessage(new MqTo(OrderConstant.OrderMQ.EXCHANGE,OrderConstant.RouteKey.TO_DELAY_QUEUE.key,orderEntity));
		
		return "OK";
	}
	
	
	
	
}
