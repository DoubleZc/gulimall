package com.zcx.gulimall.seckill.service;

import com.zcx.common.comm.CosException;
import com.zcx.gulimall.seckill.vo.SeckillSessionVo;

import java.util.List;

public interface SeckillService
{
	void upSkuLast3Day();
	
	List<SeckillSessionVo.SeckillSkuRelationVo> getCurrentSeckillSkus();
	
	List<SeckillSessionVo.SeckillSkuRelationVo> getseckillInfo(Long skuId);
	
	String kill(String killId, Integer count, String code) throws CosException;
}
