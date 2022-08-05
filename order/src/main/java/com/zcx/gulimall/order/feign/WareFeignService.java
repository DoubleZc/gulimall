package com.zcx.gulimall.order.feign;


import com.zcx.common.utils.R;
import com.zcx.gulimall.order.vo.WareSkuLockTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WareFeignService
{
	@GetMapping("/ware/waresku/hasStocks")
	R getBySkuIds(@RequestParam List<Long> skuIds);
	
	@GetMapping("/ware/wareinfo/fare")
	 R getFare(@RequestParam("addrId")Long addrId);
	
	
	@RequestMapping("/ware/waresku/lock")
	 R lockStock(@RequestBody WareSkuLockTo vo);
}
