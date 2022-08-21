package com.zcx.gulimall.car.controller;


import com.alibaba.fastjson.JSON;
import com.zcx.common.to.MemberTo;
import com.zcx.common.utils.MyThreadMsg;
import com.zcx.common.utils.R;
import com.zcx.gulimall.car.service.CartService;
import com.zcx.gulimall.car.vo.Cart;
import com.zcx.gulimall.car.vo.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.reflect.Member;
import java.util.List;

@Controller
public class CartController
{
	@Autowired
	CartService cartService;
	
	@ResponseBody
	@GetMapping("/test")
	public String test()
	{
		
		return "张三";
	}
	
	
	
	
	@ResponseBody
	@GetMapping("/listcart")
	public List<CartItem> listcart()
	{
		MemberTo msg = MyThreadMsg.getMsg(MemberTo.class);
		List<CartItem> cartById = cartService.getCartById(msg.getId());
		return cartById;
	}
	
	
	
	
	
	
	
	
	
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
