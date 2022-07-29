package com.zcx.gulimall.car.controller;


import com.alibaba.fastjson.JSON;
import com.zcx.common.constant.AuthConstant;
import com.zcx.common.utils.MyThreadMsg;
import com.zcx.gulimall.car.service.CartService;
import com.zcx.gulimall.car.vo.Cart;
import com.zcx.gulimall.car.vo.CartItem;
import com.zcx.gulimall.car.vo.UserTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
public class CartController
{
	@Autowired
	CartService cartService;
	
	
	
	
	@GetMapping("/cart.html")
	public String cartList(Model model)
	{
	
		Cart cart=cartService.getCart();
	model.addAttribute("cart",cart);

		return "cartlist";
	}


	@GetMapping("/addToCart")
	public String addCart(@RequestParam("skuId") Long skuId, @RequestParam("count")Integer count , RedirectAttributes attributes)
	{
		
		CartItem cartItem=cartService.addToCart(skuId,count);
		String s = JSON.toJSONString(cartItem);
		attributes.addAttribute("item",s);
		return "redirect:http://car.gulimall.com/addToCartSuccess";
	
	}
	
	
	@GetMapping("/addToCartSuccess")
	public String success(@RequestParam("item")String item,Model model)
	{
		
		try {
			CartItem cartItem = JSON.parseObject(item, CartItem.class);
			model.addAttribute("item",cartItem);
		} catch (Exception e) {
		
		}
		return  "success";
		
	}
	
	
	@GetMapping("/checkItem")
	public String checkItem(@RequestParam("skuId")Long skuId,@RequestParam("checked") Integer checked)
	{
		cartService.checkItem(skuId,checked);
		
		return "redirect:http://car.gulimall.com/cart.html";
	}
	
	
	@GetMapping("/countItem")
	public String countItem(@RequestParam("skuId")Long skuId,@RequestParam("count") Integer count)
	{
		cartService.countItem(skuId,count);
		
		return "redirect:http://car.gulimall.com/cart.html";
	}
	
	@GetMapping("/deleteItem")
	public String deleteItem(@RequestParam("skuId")Long skuId)
	{
		cartService.deleteItem(skuId);
		
		return "redirect:http://car.gulimall.com/cart.html";
	}
	
	
}
