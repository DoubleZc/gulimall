package com.zcx.gulimall.car.service;


import com.zcx.gulimall.car.vo.Cart;
import com.zcx.gulimall.car.vo.CartItem;

public interface CartService
{
	CartItem addToCart(Long skuId, Integer count);
	
	Cart getCart();
	
	void checkItem(Long skuId, Integer check);
	
	void countItem(Long skuId, Integer count);
	
	void deleteItem(Long skuId);
}
