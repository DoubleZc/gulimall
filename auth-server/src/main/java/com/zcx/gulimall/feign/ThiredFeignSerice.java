package com.zcx.gulimall.feign;


import com.zcx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@FeignClient("thired")
public interface ThiredFeignSerice
{
	@GetMapping("/sms")
	public R sendSmsCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
