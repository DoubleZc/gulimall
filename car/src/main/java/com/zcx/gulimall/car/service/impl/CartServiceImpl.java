package com.zcx.gulimall.car.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zcx.common.constant.CartConstant;
import com.zcx.common.utils.MyThreadMsg;
import com.zcx.common.utils.R;
import com.zcx.gulimall.car.feign.ProductFeignService;
import com.zcx.gulimall.car.service.CartService;
import com.zcx.gulimall.car.vo.Cart;
import com.zcx.gulimall.car.vo.CartItem;
import com.zcx.gulimall.car.vo.SkuInfoVo;
import com.zcx.gulimall.car.vo.UserTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
	public List<CartItem> getCartById(Long id)
	{
		BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(CartConstant.CART_PREFIX + id);
		List<CartItem> cart = getCart(operations);
		Map<Long, Integer> map = cart.stream().collect(Collectors.toMap(CartItem::getSkuId, CartItem::getCount));
		List<Long> ids = cart.stream().filter(CartItem::isCheck).map(CartItem::getSkuId).collect(Collectors.toList());
		
		
		CompletableFuture<List<CartItem>> future = CompletableFuture.supplyAsync(() -> {
			R info = productFeignService.infos(ids);
			List<SkuInfoVo> skuInfos = info.getData("skuInfos", new TypeReference<List<SkuInfoVo>>(){});
			return skuInfos.stream().map(skuInfo -> {
				CartItem cartItem = new CartItem();
				cartItem.setCheck(true);
				cartItem.setCount(map.get(skuInfo.getSkuId()));
				cartItem.setImage(skuInfo.getSkuDefaultImg());
				cartItem.setTitle(skuInfo.getSkuTitle());
				cartItem.setPrice(skuInfo.getPrice());
				cartItem.setSkuId(skuInfo.getSkuId());
				return cartItem;
			}).collect(Collectors.toList());
		}, executor);
		
		
		CompletableFuture<Map<Long, List<String>>> future1 = CompletableFuture.supplyAsync(() -> {
			return productFeignService.getAttrMap(ids);
		}, executor);
		
		try {
			cart = future.get();
			Map<Long, List<String>> map1 = future1.get();
			
			cart.forEach(i->{
				i.setSkuAttr(map1.get(i.getSkuId()));
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cart;
	}
	
	
	
	
	
	
	
	
	
	@Override
	public CartItem addToCart(Long skuId, Integer count)
	{
		
		BoundHashOperations<String, Object, Object> cartOps = getCartOps();
		Object o = cartOps.get(skuId.toString());
		
		
		if (o == null) {
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
		} else {
			CartItem cartItem = JSON.parseObject(o.toString(), CartItem.class);
			cartItem.setCount(cartItem.getCount() + count);
			cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
			return cartItem;
		}
		
		
	}
	
	
	@Override
	public Cart getCart()
	{
		UserTo user = MyThreadMsg.getMsg(UserTo.class);
		Cart cart = new Cart();
		List<CartItem> c = new ArrayList<>();
		BoundHashOperations<String, Object, Object> cartOps = getCartOps();
		
		if (user.getId() != null && user.getUserKey() != null) {
			//临时购物车数据
			BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(CartConstant.CART_PREFIX + user.getUserKey());
			List<CartItem> cartUserKey = getCart(operations);
			Map<Object, Object> cartId = cartOps.entries();
			if (!cartUserKey.isEmpty()) {
				//临时购物车有数据
				if (cartId != null) {
					//主购物车有数据
					//合并购物车
					cartUserKey.forEach(cartItem -> {
						String key = cartItem.getSkuId().toString();
						String value = JSON.toJSONString(cartItem);
						if (cartId.containsKey(key)) {
							String json = String.valueOf(cartId.get(key));
							CartItem item = JSON.parseObject(json, CartItem.class);
							item.setCount(item.getCount() + cartItem.getCount());
							String val = JSON.toJSONString(item);
							cartId.put(key, val);
						} else {
							cartId.put(key, value);
						}
					});
					
					cartOps.putAll(cartId);
				} else {
					//主购物车没数据,
					Map<String, String> collect = cartUserKey.stream().collect(Collectors.toMap(k -> k.getSkuId().toString(), JSON::toJSONString));
					cartOps.putAll(collect);
				}
				
				
				//删除临时数据
				redisTemplate.delete(CartConstant.CART_PREFIX + user.getUserKey());
				
			}
		}
		
		
		c = getCart(cartOps);
		cart.setItems(c);
		cart.setTotalAmount(cart.getTotalAmount());
		return cart;
	}
	
	@Override
	public void checkItem(Long skuId, Integer check)
	{
		CartItem item = getItem(skuId);
		item.setCheck(check == 1);
		
		setItem(skuId.toString(), item);
	}
	
	@Override
	public void countItem(Long skuId, Integer count)
	{
		CartItem item = getItem(skuId);
		item.setCount(count);
		setItem(skuId.toString(), item);
	}
	
	@Override
	public void deleteItem(Long skuId)
	{
		BoundHashOperations<String, Object, Object> cartOps = getCartOps();
		cartOps.delete(skuId.toString());
	}
	
	private void setItem(String key, CartItem value)
	{
		BoundHashOperations<String, Object, Object> cartOps = getCartOps();
		String s = JSON.toJSONString(value);
		cartOps.put(key, s);
	}
	
	
	private CartItem getItem(Long skuId)
	{
		BoundHashOperations<String, Object, Object> cartOps = getCartOps();
		Object o = cartOps.get(skuId.toString());
		String json = String.valueOf(o);
		return JSON.parseObject(json, CartItem.class);
		
	}
	
	
	private List<CartItem> getCart(BoundHashOperations<String, Object, Object> cartOps)
	{
		
		List<Object> values = cartOps.values();
		if (values != null && !values.isEmpty()) {
			
			return values.stream().map(m -> {
				CartItem cartItem = new CartItem();
				cartItem = JSON.parseObject(String.valueOf(m), CartItem.class);
				return cartItem;
			}).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}
	
	
	/***
	 *
	 * 获取要操作的购物车
	 * @return
	 */
	private BoundHashOperations<String, Object, Object> getCartOps()
	{
		
		UserTo user = MyThreadMsg.getMsg(UserTo.class);
		
		String cartKey = "";
		if (user.getId() != null) {
			
			cartKey = CartConstant.CART_PREFIX + user.getId();
		} else {
			cartKey = CartConstant.CART_PREFIX + user.getUserKey();
		}
		
		log.info("redis中存储的用户key：{}", cartKey);
		return redisTemplate.boundHashOps(cartKey);
		
	}
}
