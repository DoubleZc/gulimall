package com.zcx.gulimall.search.config;


import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.zcx.common.utils.MyRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class MySessionConfig
{
	@Bean
	public CookieSerializer cookieSerializer()
	{
		DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
		cookieSerializer.setDomainName("gulimall.com");
		cookieSerializer.setCookieName("GULISESSION");
		return cookieSerializer;

	}


//	不带泛型
	@Bean
	public RedisSerializer springSessionDefaultRedisSerializer() {
		return new GenericFastJsonRedisSerializer();
	}


}
