package com.zcx.gulimall.order.interceptor;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class FeignInterceptor
{
	
	@Bean
	public RequestInterceptor requestInterceptor()
	{
		return new RequestInterceptor()
		{
			@Override
			public void apply(RequestTemplate template)
			{
				
				ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
				if (requestAttributes != null) {
					HttpServletRequest request = requestAttributes.getRequest();
					String cookie = request.getHeader("Cookie");
					template.header("Cookie", cookie);
				}
			}
		};
	}
	
	
}
