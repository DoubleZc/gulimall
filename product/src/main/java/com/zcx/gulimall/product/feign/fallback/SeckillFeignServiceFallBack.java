package com.zcx.gulimall.product.feign.fallback;


import com.zcx.common.utils.ExceptionCode;
import com.zcx.common.utils.R;
import com.zcx.gulimall.product.feign.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class SeckillFeignServiceFallBack implements SeckillFeignService
{
	
	@Override
	public R getseckillInfo(Long skuId)
	{
		log.error("seckill的熔断方法");
		return R.error(ExceptionCode.TOO_MANY_REQUEST);
	}
}
