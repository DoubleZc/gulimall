package com.zcx.gulimall.member.fegin;


import com.zcx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@FeignClient("gulimall-order")
public interface OrderFeignService
{
	@RequestMapping("/order/order/listitem")
	 R listitem(@RequestBody Map<String, Object> params);
}
