package com.zcx.gulimall.order.web;


import com.zcx.common.constant.OrderConstant;
import com.zcx.gulimall.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
	RabbitTemplate rabbitTemplate;

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
		rabbitTemplate.convertAndSend(OrderConstant.OrderMQ.EXCHANGE,OrderConstant.RouteKey.TO_DELAY_QUEUE.key,orderEntity);
		return "OK";
	}
	
	
	
	
}
