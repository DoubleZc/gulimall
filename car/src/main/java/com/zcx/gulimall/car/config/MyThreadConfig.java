package com.zcx.gulimall.car.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class MyThreadConfig
{
	@Bean
	public ThreadPoolExecutor threadPoolExecutor(ThreadPoolProperties properties)
	{
		return new ThreadPoolExecutor(
				properties.getCoreSize(),
				properties.getMaximumPoolSize(),
				properties.getKeepAliveTime(),
				TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(properties.getQueueSize()),
				Executors.defaultThreadFactory(),
				new ThreadPoolExecutor.AbortPolicy()
		);
	}
}
