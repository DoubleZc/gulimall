package com.zcx.gulimall;

import com.zcx.gulimall.product.ProductApplication;
import com.zcx.gulimall.product.entity.BrandEntity;
import com.zcx.gulimall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = ProductApplication.class)
class ProductApplicationTests {

    @Autowired
    private BrandService brandService;


    @Test
    void contextLoads() {





    }

}
