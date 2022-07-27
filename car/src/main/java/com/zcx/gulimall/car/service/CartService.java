package com.zcx.gulimall.car.service;


import com.zcx.gulimall.car.vo.CartItem;

public interface CartService
{
	CartItem addToCart(Long skuId, Integer count);
}
