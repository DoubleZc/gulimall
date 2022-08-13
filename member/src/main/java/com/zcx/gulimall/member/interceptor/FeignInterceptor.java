package com.zcx.gulimall.member.interceptor;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Configuration
public class FeignInterceptor
{
	@Bean
	public RequestInterceptor requestInterceptor()
	{
	
	return 	new RequestInterceptor()
		{
			@Override
			public void apply(RequestTemplate template)
			{
				ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
				if (attributes!=null) {
					HttpServletRequest request = attributes.getRequest();
					String cookie = request.getHeader("Cookie");
					log.info("远程调用存入的Cookie：{}",cookie);
					template.header("Cookie", cookie);
				}
			}
		};
		
		
	}
	
	
	
	
}
