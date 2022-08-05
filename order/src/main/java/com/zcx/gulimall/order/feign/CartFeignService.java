package com.zcx.gulimall.order.feign;


import com.zcx.gulimall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("gulimall-car")
public interface CartFeignService
{
	@GetMapping("/listcart")
	List<OrderItemVo> listcart();
	
	
	
	
}
