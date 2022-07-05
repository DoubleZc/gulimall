package com.zcx.gulimall.ware.feign;

import com.zcx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient("gulimall-product")
public interface ProductFeign
{
	@RequestMapping("/product/skuinfo/one")
	 R getById(@RequestBody Long id);
}
