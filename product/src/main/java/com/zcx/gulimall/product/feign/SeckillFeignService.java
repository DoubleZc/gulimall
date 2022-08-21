package com.zcx.gulimall.product.feign;

import com.zcx.common.utils.R;
import com.zcx.gulimall.product.feign.fallback.SeckillFeignServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(value = "gulimall-seckill",fallback = SeckillFeignServiceFallBack.class)
public interface SeckillFeignService
{
	@GetMapping("/seckillinfo/{skuId}")
	R getseckillInfo(@PathVariable("skuId") Long skuId);
}
