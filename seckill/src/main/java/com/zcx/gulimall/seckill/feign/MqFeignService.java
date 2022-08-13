package com.zcx.gulimall.seckill.feign;


import com.zcx.common.to.mq.MqTo;
import com.zcx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-mqsave")
public interface MqFeignService
{
	@RequestMapping("/send")
	 R sendMessage(@RequestBody MqTo entity);
	
	
}
