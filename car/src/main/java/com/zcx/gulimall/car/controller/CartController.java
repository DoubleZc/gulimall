package com.zcx.gulimall.car.controller;


import com.zcx.common.constant.AuthConstant;
import com.zcx.common.utils.MyThreadMsg;
import com.zcx.gulimall.car.service.CartService;
import com.zcx.gulimall.car.vo.CartItem;
import com.zcx.gulimall.car.vo.UserTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class CartController
{
	@Autowired
	CartService cartService;
	
	
	@GetMapping("/cart.html")
	public String cartList()
	{
		UserTo userTo= MyThreadMsg.getMsg(UserTo.class);

		return "cartlist";
	}


	@GetMapping("/addToCart")
	public String addCart(@RequestParam("skuId") Long skuId, @RequestParam("count")Integer count , Model model)
	{
		
		CartItem cartItem=cartService.addToCart(skuId,count);
		
		model.addAttribute("item",cartItem);
		return  "success";
	
	}
	

}
