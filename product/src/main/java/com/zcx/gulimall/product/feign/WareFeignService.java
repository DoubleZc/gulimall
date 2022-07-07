package com.zcx.gulimall.product.feign;


import com.zcx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-ware")
public interface WareFeignService
{
	@GetMapping("/ware/waresku/{skuId}")
	R getBySkuId(@PathVariable(name = "skuId") Long skuId);



}
