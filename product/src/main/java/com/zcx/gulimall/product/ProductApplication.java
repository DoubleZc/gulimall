package com.zcx.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**    配置cache；
 * 1.导入坐标spring-boot-starter-cache
 * 2.开启  @EnableCaching
 * 3.需要管理的缓存返回值，方法上加@Cacheable
 * @Cacheable(value = {"categorys"},key ="#root.method.name" )
 * value为存入缓存的区名区名   key为缓存中key的名字  可用#root拿到注解的方法
 * 4.修改配置类  MycacheConfig
 *
 *
 *
 *
 *
 */



@EnableCaching

//@MapperScan("com.zcx.gulimall.product.dao")
@SpringBootApplication
@EnableFeignClients
@EnableRedisHttpSession
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }

}
