package com.zcx.gulimall.coupon.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.common.to.SkuInfoTo;
import com.zcx.common.utils.ExceptionCode;
import com.zcx.common.utils.R;
import com.zcx.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.zcx.gulimall.coupon.feign.ProductFeignService;
import com.zcx.gulimall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.Query;

import com.zcx.gulimall.coupon.dao.SeckillSessionDao;
import com.zcx.gulimall.coupon.entity.SeckillSessionEntity;
import com.zcx.gulimall.coupon.service.SeckillSessionService;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

	@Autowired
	SeckillSkuRelationService relationService;
	
	@Autowired
	ProductFeignService feignService;
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }
	
	@Override
	public R getSkuLate3Dys()
	{
		
		SeckillSessionDao baseMapper = this.baseMapper;
		
		
		//当天的0点
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		Date start = calendar.getTime();
		
		
		//后天的23:59:59
		calendar.add(Calendar.DAY_OF_YEAR,3);
		calendar.add(Calendar.SECOND,-1);
		Date end = calendar.getTime();
		
		List<SeckillSessionEntity> list =baseMapper.getSkuLate3Dys(start, end);
		if (list.isEmpty())
		{
			return R.error("无秒杀商品");
		}
		List<Long> longs = list.stream().map(SeckillSessionEntity::getId).collect(Collectors.toList());
		//查询所有
		List<SeckillSkuRelationEntity> relationEntities = relationService.list(new LambdaQueryWrapper<SeckillSkuRelationEntity>().in(SeckillSkuRelationEntity::getPromotionSessionId, longs));
		List<Long> skuIds = relationEntities.stream().map(SeckillSkuRelationEntity::getSkuId).collect(Collectors.toList());
		
		
		R infos = null;
		try {
			infos = feignService.infos(skuIds);
		} catch (Exception e) {
			return R.error(ExceptionCode.FEIGN_EXCEPTION);
		}
		
		
		List<SkuInfoTo> data = infos.getData("skuInfos", new TypeReference<List<SkuInfoTo>>(){});
		
		
		
		Map<Long, SkuInfoTo> map = data.stream().collect(Collectors.toMap(SkuInfoTo::getSkuId,i->i));
		
		relationEntities.forEach(i->i.setSkuInfoTo(map.get(i.getSkuId())));
		
		
		//分组
		Map<Long, List<SeckillSkuRelationEntity>> stringListMap = relationEntities.stream().collect(Collectors.groupingBy(SeckillSkuRelationEntity::getPromotionSessionId));
		
		
		//封装list
		list.forEach(seckillSessionEntity -> seckillSessionEntity.setSkuRelationEntities(stringListMap.get(seckillSessionEntity.getId())));
		
		return R.ok().put("data",list);
		
	}
	
	
	
	
}