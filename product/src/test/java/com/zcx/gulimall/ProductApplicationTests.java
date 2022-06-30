package com.zcx.gulimall;

import com.zcx.gulimall.product.ProductApplication;
import com.zcx.gulimall.product.service.BrandService;
import com.zcx.gulimall.product.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest(classes = ProductApplication.class)
class ProductApplicationTests {

    @Autowired
    private CategoryService categoryService;


    @Test
    void contextLoads() throws Exception {

        Long[] path = categoryService.findPath(225L);
        System.out.println(Arrays.toString(path));


    }

}
