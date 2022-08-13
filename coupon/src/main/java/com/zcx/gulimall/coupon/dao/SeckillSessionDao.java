package com.zcx.gulimall.coupon.dao;

import com.zcx.gulimall.coupon.entity.SeckillSessionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 秒杀活动场次
 * 
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-21 17:32:50
 */
@Mapper
public interface SeckillSessionDao extends BaseMapper<SeckillSessionEntity> {
	
	List<SeckillSessionEntity> getSkuLate3Dys(@Param("start") Date start, @Param("end") Date end);
}
