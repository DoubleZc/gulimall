package com.zcx.gulimall;

import com.zcx.gulimall.product.ProductApplication;
import com.zcx.gulimall.product.service.BrandService;
import com.zcx.gulimall.product.service.CategoryService;
import com.zcx.gulimall.product.vo.Images;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = ProductApplication.class)
class ProductApplicationTests {

    @Autowired
    private CategoryService categoryService;


    @Test
    void contextLoads() throws Exception
    {
        Images a = new Images();
        Images b = new Images();
        a.setDefaultImg(1);
        b.setDefaultImg(2);

        List<Images> c=new ArrayList<>();
        c.add(a);
        c.add(b);

        List<Images> collect = c.stream().filter(item -> item.getDefaultImg() == 3).collect(Collectors.toList());

        System.out.println(collect.isEmpty());



    }

}
