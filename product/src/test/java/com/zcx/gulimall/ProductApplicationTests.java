package com.zcx.gulimall;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zcx.common.constant.WareConstant;
import com.zcx.common.utils.R;
import com.zcx.gulimall.product.ProductApplication;
import com.zcx.gulimall.product.feign.WareFeignService;
import com.zcx.gulimall.product.service.BrandService;
import com.zcx.gulimall.product.service.CategoryService;
import com.zcx.gulimall.product.vo.Images;
import net.minidev.json.JSONObject;
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


    @Autowired
    WareFeignService wareFeignService;

    @Test
    void contextLoads() throws Exception
    {
        R bySkuId = wareFeignService.getBySkuId(35L);


        boolean data = (boolean)bySkuId.get("data");

        System.out.println(WareConstant.Stock.NOT_HAVE);
        System.out.println(data);


    }

}
