package com.zcx.common.config.springsession;


import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.zcx.common.constant.ComConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
class MySessionConfig
{
	static {
		System.out.println("=========================MySessionConfig==============================");
		
	}
	@Bean
	public CookieSerializer cookieSerializer()
	{
		
		DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
		cookieSerializer.setDomainName(ComConstant.SESSION_DOMAIN);
		cookieSerializer.setCookieName(ComConstant.SESSION_COOKIE);
		return cookieSerializer;
	}


	@Bean
	public RedisSerializer springSessionDefaultRedisSerializer() {
		return new GenericFastJsonRedisSerializer();
	}

}
