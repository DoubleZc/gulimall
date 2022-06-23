package com.zcx.gulimall;

import com.zcx.common.utils.R;
import com.zcx.gulimall.member.MemberApplication;
import com.zcx.gulimall.member.fegin.CouponFeginService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest(classes = MemberApplication.class)
class MemberApplicationTests {

    @Autowired
    private CouponFeginService couponFeginService;



    @Test
    void contextLoads() {



    }

}
