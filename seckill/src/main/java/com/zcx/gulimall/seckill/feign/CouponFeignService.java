package com.zcx.gulimall.seckill.feign;


import com.zcx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-coupon")
public interface CouponFeignService
{
	
	@RequestMapping("/coupon/seckillsession/getskulate3days")
	 R getSkuLate3Dys();


}
