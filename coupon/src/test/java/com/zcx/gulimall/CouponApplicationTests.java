package com.zcx.gulimall;

import com.zcx.common.utils.R;
import com.zcx.gulimall.coupon.CouponApplication;
import com.zcx.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.zcx.gulimall.coupon.service.SeckillSkuRelationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = CouponApplication.class)
class CouponApplicationTests {

    @Autowired
    SeckillSkuRelationService relationService;
    
    @Test
    void contextLoads() {
    
        List<SeckillSkuRelationEntity> list = relationService.list();
        Map<String, List<SeckillSkuRelationEntity>> stringListMap = R.torMap(list, i -> i.getPromotionSessionId().toString());
        
        stringListMap.forEach(((s, seckillSkuRelationEntities) -> {
            System.out.println("s = " + s);
            System.out.println("seckillSkuRelationEntities = " + seckillSkuRelationEntities);
        }));
        
        
    }

}
