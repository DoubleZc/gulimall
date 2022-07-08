package com.zcx.gulimall;

import com.zcx.gulimall.ware.WareApplication;
import com.zcx.gulimall.ware.service.WareSkuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = WareApplication.class)
class WareApplicationTests {
@Autowired
    WareSkuService wareSkuService;

    @Test
    void contextLoads() {

        Integer bySkuId = wareSkuService.getBySkuId(35L);
        System.out.println(bySkuId);


    }

}
