package com.zcx.common.utils;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public  class MyThreadMsg
{
	public static ThreadLocal<String> threadLocal=new ThreadLocal<>();
	
	
	public static<T> T getMsg(Class<T> c)
	{
		return JSON.parseObject(threadLocal.get(), c);
	}
	
	
	public static<T>  void setMsg(T tClass)
	{
		String s = JSON.toJSONString(tClass);
		log.info("本地线程{}存储数据:{}",Thread.currentThread(),s);
		threadLocal.set(s);
	}
}
