package com.zcx.gulimall.member.config;

import com.zcx.gulimall.member.interceptor.LoginInterceptor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@Configuration
@ComponentScan("com.zcx.common.config.springsession")
public class WebMvcConfig implements WebMvcConfigurer
{
	@Override
	public void addInterceptors(InterceptorRegistry registry)
	{
		registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**");
	}
}
