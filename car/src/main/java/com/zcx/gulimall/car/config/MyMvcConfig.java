package com.zcx.gulimall.car.config;

import com.zcx.gulimall.car.interceptor.MyInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


@Configuration
public class MyMvcConfig extends WebMvcConfigurationSupport
{
	@Override
	protected void addInterceptors(InterceptorRegistry registry)
	{
		registry.addInterceptor(new MyInterceptor()).addPathPatterns("/**");
	}
}
