package com.zcx.gulimall.order.feign;


import com.zcx.common.utils.R;
import com.zcx.gulimall.order.vo.MemberAddressVo;
import com.zcx.gulimall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("gulimall-member")
public interface MemberFeignService
{
	
	@RequestMapping("/member/memberreceiveaddress/address")
	 List<MemberAddressVo> listAddress(@RequestParam("id") Long id);
	
	
	@RequestMapping("/member/memberreceiveaddress/info/{id}")
	 R info(@PathVariable("id") Long id);
	
}
