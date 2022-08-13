package com.zcx.gulimall.product.feign;

import com.zcx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient("gulimall-seckill")
public interface SeckillFeignService
{
	@GetMapping("/seckillinfo/{skuId}")
	R getseckillInfo(@PathVariable("skuId") Long skuId);
}
