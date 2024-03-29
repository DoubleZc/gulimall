/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.zcx.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;


	public R() {
		put("code", 0);
		put("msg", "success");
	}
	
	public static R error() {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
	}
	
	public static R error(String msg) {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
	}
	
	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static R error(ExceptionCode code) {
		R r = new R();
		r.put("code", code.getCode());
		r.put("msg", code.getMsg());
		return r;
	}


	public static R ok(ExceptionCode code) {
		R r = new R();
		r.put("code", code.getCode());
		r.put("msg", code.getMsg());
		return r;
	}


	public static R ok(String msg) {
		R r = new R();
		r.put("msg", msg);
		return r;
	}
	
	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}
	
	public static R ok() {
		return new R();
	}

	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}


	public  Integer getCode()
	{
		return (Integer) super.get("code");
	}
	
	public<T>  T getData(String key,Class<T> tClass)
	{
		Object o = get(key);
		//对象序列化转换异常将对象转换成json纯字符串，再转成对象
		String s = JSON.toJSONString(o);
		return JSON.parseObject(s, tClass);
	}
	
	
	public <T> T getData(String key,TypeReference<T> t)
	{
		Object o = get(key);
		String s = JSON.toJSONString(o);
		return JSON.parseObject(s, t);
	}
	
	
	public static <T> Map<String, List<T>> torMap(List<T> list, Function<T,String> keyFun)
	{
		Map <String,List<T>> map = new HashMap<>();
		list.forEach(
				i->
				{
					String key = keyFun.apply(i);
					if (map.containsKey(key))
					{
						List<T> ts = map.get(key);
						ts.add(i);
					}else
					{
						List<T>ts=new ArrayList<>();
						ts.add(i);
						map.put(key,ts);
					}
				}
		);
		return map;
	}
	
	
	
	
}
