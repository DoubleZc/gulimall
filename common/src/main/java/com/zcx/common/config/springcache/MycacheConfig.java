package com.zcx.common.config.springcache;


import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;


@Configuration
@EnableCaching
public class MycacheConfig
{


	//缓存配置
	@Bean
	public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties)
	{
		//拿到配置文件中的配置信息
		CacheProperties.Redis redisProperties = cacheProperties.getRedis();
		//拿到默认配置config
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
		//添加值得序列化器
		config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer() ));
		//将配置文件中的配置信息注入到配置中
		if (redisProperties.getTimeToLive() != null) {
			config = config.entryTtl(redisProperties.getTimeToLive());
		}

		if (redisProperties.getKeyPrefix() != null) {
			config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
		}

		if (!redisProperties.isCacheNullValues()) {
			config = config.disableCachingNullValues();
		}

		if (!redisProperties.isUseKeyPrefix()) {
			config = config.disableKeyPrefix();
		}

		return config;

	}




}
