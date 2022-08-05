package com.zcx.gulimall.order.feign;


import com.zcx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-product")
public interface ProductFeignService
{
	@RequestMapping("/product/skuinfo/spuInfo")
	R getSpuInfo(@RequestBody Long skuId);
	
	
}
