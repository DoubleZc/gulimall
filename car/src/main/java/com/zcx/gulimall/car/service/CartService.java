package com.zcx.gulimall.car.service;


import com.zcx.gulimall.car.vo.Cart;
import com.zcx.gulimall.car.vo.CartItem;

import java.util.List;

public interface CartService
{
	CartItem addToCart(Long skuId, Integer count);
	
	Cart getCart();
	
	List<CartItem> getCartById(Long id);
	
	void checkItem(Long skuId, Integer check);
	
	void countItem(Long skuId, Integer count);
	
	void deleteItem(Long skuId);
	
	
	
}
