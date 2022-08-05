package com.zcx.gulimall.ware.feign;

import com.zcx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-order")
public interface OrderFeignService
{
	
	@RequestMapping("/order/order/feign/status")
	R getStatus(String orderSn);


}
