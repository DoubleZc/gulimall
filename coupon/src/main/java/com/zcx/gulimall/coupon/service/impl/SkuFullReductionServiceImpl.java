package com.zcx.gulimall.coupon.service.impl;

import com.zcx.common.to.MemberPrice;
import com.zcx.common.to.SkuReductionTo;
import com.zcx.gulimall.coupon.entity.MemberPriceEntity;
import com.zcx.gulimall.coupon.entity.SkuLadderEntity;
import com.zcx.gulimall.coupon.service.MemberPriceService;
import com.zcx.gulimall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.ExtendedBeanInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.Query;

import com.zcx.gulimall.coupon.dao.SkuFullReductionDao;
import com.zcx.gulimall.coupon.entity.SkuFullReductionEntity;
import com.zcx.gulimall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

	@Autowired
	SkuLadderService skuLadderService;

	@Autowired
	MemberPriceService memberPriceService;




    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public void saveSkuReduction(SkuReductionTo skuReductionTo)
	{
		SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
		BeanUtils.copyProperties(skuReductionTo,skuLadderEntity);
		if(skuLadderEntity.getFullCount()>0)
		skuLadderService.save(skuLadderEntity);


		SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
		BeanUtils.copyProperties(skuReductionTo,skuFullReductionEntity);
		if (skuFullReductionEntity.getReducePrice().compareTo(BigDecimal.valueOf(0))>0)
		save(skuFullReductionEntity);
		//sku优惠满减信息gulimall_sms  /sku_Ladder/full_reduction/member_price
		List<MemberPrice> memberPrices = skuReductionTo.getMemberPrice();
		if (!memberPrices.isEmpty()) {
			List<MemberPriceEntity> collect = memberPrices.stream().map(item -> {
				MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
				memberPriceEntity.setMemberLevelId(item.getId());
				memberPriceEntity.setMemberLevelName(item.getName());
				memberPriceEntity.setMemberPrice(item.getPrice());
				memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
				return memberPriceEntity;
			}).collect(Collectors.toList());
			memberPriceService.saveBatch(collect);
		}
	}

}