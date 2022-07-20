package com.zcx.gulimall;

import com.zcx.gulimall.product.ProductApplication;
import com.zcx.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.zcx.gulimall.product.entity.AttrGroupEntity;
import com.zcx.gulimall.product.service.AttrService;
import com.zcx.gulimall.product.service.ProductAttrValueService;
import com.zcx.gulimall.product.service.SkuInfoService;
import com.zcx.gulimall.product.vo.SkuItemVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest(classes = ProductApplication.class)
class ProductApplicationTests {



    @Autowired
    StringRedisTemplate stringRedisTemplate;



    @Autowired
    SkuInfoService skuInfoService;


    @Test
    void contextLoads() throws Exception
    {
        SkuItemVo item = skuInfoService.item(35L);
        System.out.println(item);



    }

}
