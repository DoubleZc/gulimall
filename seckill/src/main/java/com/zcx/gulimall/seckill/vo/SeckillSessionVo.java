package com.zcx.gulimall.seckill.vo;

import com.zcx.common.to.SkuInfoTo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Data
public class SeckillSessionVo implements Serializable
{
	private Long id;
	/**
	 * 场次名称
	 */
	private String name;
	/**
	 * 每日开始时间
	 */
	private Date startTime;
	/**
	 * 每日结束时间
	 */
	private Date endTime;
	/**
	 * 启用状态
	 */
	private Integer status;
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	
	private List<SeckillSkuRelationVo> skuRelationEntities;
	
	
	@Data
	public static class SeckillSkuRelationVo
	{
		
		private Long id;
		/**
		 * 活动id
		 */
		private Long promotionId;
		/**
		 * 活动场次id
		 */
		private Long promotionSessionId;
		/**
		 * 商品id
		 */
		private Long skuId;
		/**
		 * 秒杀价格
		 */
		private BigDecimal seckillPrice;
		/**
		 * 秒杀总量
		 */
		private Integer seckillCount;
		/**
		 * 每人限购数量
		 */
		private Integer seckillLimit;
		/**
		 * 排序
		 */
		private Integer seckillSort;
		
		
		
		
		
		private Long startTime;
		private Long endTime;
		
		/**
		 * 秒杀随机码
		 */
		private String uuid;
		
		
		
		SkuInfoTo skuInfoTo;
		
	}
	
	
	
}
