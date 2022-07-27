package com.zcx.gulimall.car.feign;


import com.zcx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("gulimall-product")
public interface ProductFeignService
{
	@RequestMapping("/product/skuinfo/info/{skuId}")
	 R info(@PathVariable("skuId") Long skuId);
	
	
	@GetMapping("/product/skusaleattrvalue/attrlist/{skuId}")
	 List<String> getAttrList(@PathVariable("skuId") Long skuId);
}
