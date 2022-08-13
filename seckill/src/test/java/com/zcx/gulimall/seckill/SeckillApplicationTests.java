package com.zcx.gulimall.seckill;

import com.zcx.gulimall.seckill.feign.CouponFeignService;
import com.zcx.gulimall.seckill.service.SeckillService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SeckillApplication.class)
class SeckillApplicationTests
{
	@Autowired
	CouponFeignService couponFeignService;
	
	
	@Autowired
	SeckillService seckillService;
	
	@Test
	void contextLoads()
	{
		seckillService.upSkuLast3Day();
		
	}
	
}
