package com.zcx.gulimall.car.service.impl;


import com.alibaba.fastjson.JSON;
import com.zcx.common.constant.CartConstant;
import com.zcx.common.utils.CheckException;
import com.zcx.common.utils.ExceptionCode;
import com.zcx.common.utils.MyThreadMsg;
import com.zcx.common.utils.R;
import com.zcx.gulimall.car.feign.ProductFeignService;
import com.zcx.gulimall.car.service.CartService;
import com.zcx.gulimall.car.vo.CartItem;
import com.zcx.gulimall.car.vo.SkuInfoVo;
import com.zcx.gulimall.car.vo.UserTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;


@Slf4j
@Service
public class CartServiceImpl implements CartService
{
	@Autowired
	StringRedisTemplate redisTemplate;
	
	
	@Autowired
	ProductFeignService productFeignService;
	
	
	@Autowired
	ThreadPoolExecutor executor;
	
	@Override
	public CartItem addToCart(Long skuId, Integer count)
	{
		
		BoundHashOperations<String, Object, Object> cartOps = getCartOps();
		Object o = cartOps.get(skuId.toString());
		
		
		if (o==null){
			CartItem cartItem = new CartItem();
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				R info = productFeignService.info(skuId);
				SkuInfoVo skuInfo = info.getData("skuInfo", SkuInfoVo.class);
				cartItem.setCheck(true);
				cartItem.setCount(count);
				cartItem.setImage(skuInfo.getSkuDefaultImg());
				cartItem.setTitle(skuInfo.getSkuTitle());
				cartItem.setPrice(skuInfo.getPrice());
				cartItem.setSkuId(skuId);
			}, executor);
			
			CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
				List<String> attrList = productFeignService.getAttrList(skuId);
				cartItem.setSkuAttr(attrList);
			}, executor);
			
			CompletableFuture<Void> future2 = future1.runAfterBothAsync(future, () -> {
				cartOps.put(String.valueOf(skuId), JSON.toJSONString(cartItem));
			}, executor);
			try {
				future2.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return cartItem;
		}else
		{
			CartItem cartItem = JSON.parseObject(o.toString(), CartItem.class);
			cartItem.setCount(cartItem.getCount()+count);
			cartOps.put(skuId.toString(),JSON.toJSONString(cartItem));
			return cartItem;
		}
		
		
		
	
		
	}
	
	
	/***
	 *
	 * 获取要操作的购物车
	 * @return
	 */
	private BoundHashOperations<String, Object, Object> getCartOps()
	{
		
		UserTo user = MyThreadMsg.getMsg(UserTo.class);
		
		String cartKey="";
		if (user.getId()!=null)
		{
			
			cartKey= CartConstant.CART_PREFIX+user.getId();
		}else {
			cartKey = CartConstant.CART_PREFIX + user.getUserKey();
		}
		
		log.info("redis中存储的用户key：{}",cartKey);
		return redisTemplate.boundHashOps(cartKey);
		
	}
}
