package com.zcx.gulimall.member.fegin;


import com.zcx.common.utils.R;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;



@FeignClient("gulimall-coupon")
public interface CouponFeginService {

    @RequestMapping("/coupon/coupon/member/list")
    public R memeberCoupon() ;


}
