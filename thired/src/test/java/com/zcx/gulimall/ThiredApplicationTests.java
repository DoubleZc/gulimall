package com.zcx.gulimall;



import com.zcx.gulimall.controller.SmsController;
import com.zcx.gulimall.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class ThiredApplicationTests {


    @Autowired
    SmsController smsController;

    @Test
    void contextLoads() {



    }

}
