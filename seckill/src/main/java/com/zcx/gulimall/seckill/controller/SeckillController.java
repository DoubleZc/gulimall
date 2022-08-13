package com.zcx.gulimall.seckill.controller;


import com.zcx.common.comm.CosException;
import com.zcx.common.utils.R;
import com.zcx.gulimall.seckill.service.SeckillService;
import com.zcx.gulimall.seckill.vo.SeckillSessionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SeckillController
{
	@Autowired
	SeckillService seckillService;
	
	
	@RequestMapping("/currentskus")
	public R getCurrentSeckillSkus(){
	 List<SeckillSessionVo.SeckillSkuRelationVo>skus= seckillService.getCurrentSeckillSkus();
	 return R.ok().put("data",skus);
	}
	
	@GetMapping("/seckillinfo/{skuId}")
	public R getseckillInfo(@PathVariable("skuId") Long skuId){
		
		List<SeckillSessionVo.SeckillSkuRelationVo> info= seckillService.getseckillInfo(skuId);
		return R.ok().put("data",info);
	
	}
	
	
	@GetMapping("/kill")
	public R kill(@RequestParam("killId") String killId,
			@RequestParam("count") Integer count,
			@RequestParam("code") String code)
	{
		String orderSn=null;
		try {
			 orderSn= seckillService.kill(killId,count,code);
		} catch (CosException e) {
			e.printStackTrace();
		}
		
		if (orderSn!=null)
		{
			return  R.ok().put("data",orderSn);
		}else
		{
			return R.error("秒杀失败");
		}
		
		
	}
	


}
