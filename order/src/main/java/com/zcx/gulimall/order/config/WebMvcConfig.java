package com.zcx.gulimall.order.config;

import com.zcx.gulimall.order.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebMvcConfig  extends WebMvcConfigurationSupport
{
	@Override
	protected void addInterceptors(InterceptorRegistry registry)
	{
		registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**");
	}
}
