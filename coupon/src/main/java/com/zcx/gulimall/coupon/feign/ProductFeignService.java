package com.zcx.gulimall.coupon.feign;


import com.zcx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("gulimall-product")
public interface ProductFeignService
{
	@RequestMapping("/product/skuinfo/infos")
	R infos(@RequestBody List<Long> skuIds);
}
