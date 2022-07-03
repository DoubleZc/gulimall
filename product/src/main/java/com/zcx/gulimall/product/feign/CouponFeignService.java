package com.zcx.gulimall.product.feign;


import com.zcx.common.to.SkuReductionTo;
import com.zcx.common.to.SpuBoundTo;
import com.zcx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-coupon")
public interface CouponFeignService
{
	@RequestMapping("/coupon/spubounds/save")
	R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

	@RequestMapping("/coupon/skufullreduction/saveInfo")
	R saveSkuReduction(@RequestBody  SkuReductionTo skuReductionTo);
}
