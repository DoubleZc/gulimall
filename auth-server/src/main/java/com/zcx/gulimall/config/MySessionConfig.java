package com.zcx.gulimall.config;


import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.zcx.common.constant.ComConstant;
import com.zcx.common.utils.MyRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
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
		cookieSerializer.setDomainName(ComConstant.SESSION_DOMAIN);
		cookieSerializer.setCookieName(ComConstant.SESSION_COOKIE);
		return cookieSerializer;
	}


//	不带泛型
	@Bean
	public RedisSerializer springSessionDefaultRedisSerializer() {
		return new GenericFastJsonRedisSerializer();
	}


}
