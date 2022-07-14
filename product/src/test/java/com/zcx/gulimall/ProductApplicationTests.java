package com.zcx.gulimall;

import com.zcx.gulimall.product.ProductApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;

@Slf4j
@SpringBootTest(classes = ProductApplication.class)
class ProductApplicationTests {



    @Autowired
    StringRedisTemplate stringRedisTemplate;



    @Autowired
    RedissonClient redissonClient;


    @Test
    void contextLoads() throws Exception
    {

        System.out.println(redissonClient);

    }

}
