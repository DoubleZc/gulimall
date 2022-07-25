package com.zcx.gulimall.config;



import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.IOException;

@Configuration
public class MyRedissonConfig
{

	@Bean(destroyMethod = "shutdown")
	RedissonClient redisson() throws IOException
	{
		Config config = new Config();
		config.useSingleServer().setAddress("redis://192.168.182.128:6379");
		RedissonClient redissonClient = Redisson.create(config);
		return redissonClient;
	}


}
