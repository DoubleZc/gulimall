package com.zcx.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.gulimall.product.vo.SkuItemVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.Query;

import com.zcx.gulimall.product.dao.SkuSaleAttrValueDao;
import com.zcx.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.zcx.gulimall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public List<SkuItemVo.SkuSaleAttr> listBySkuId(Long skuId)
	{
		List<SkuSaleAttrValueEntity> list = list(new LambdaQueryWrapper<SkuSaleAttrValueEntity>().eq(SkuSaleAttrValueEntity::getSkuId, skuId));
		return list.stream().map(t -> {
			SkuItemVo.SkuSaleAttr skuSaleAttr = new SkuItemVo.SkuSaleAttr();
			skuSaleAttr.setAttrId(t.getAttrId());
			skuSaleAttr.setAttrName(t.getAttrName());
			skuSaleAttr.setAttrValue(t.getAttrValue());
			return skuSaleAttr;
		}).collect(Collectors.toList());
	}

	@Override
	public Map<Long, List<SkuItemVo.SkuSaleAttr>> MapBySpuId(List<Long> collect)
	{
		List<SkuSaleAttrValueEntity> list = list(new LambdaQueryWrapper<SkuSaleAttrValueEntity>().in(SkuSaleAttrValueEntity::getSkuId, collect));
		Map<Long, List<SkuItemVo.SkuSaleAttr>> spuAttrs=new HashMap<>();
		list.forEach(m->{
			SkuItemVo.SkuSaleAttr skuSaleAttr = new SkuItemVo.SkuSaleAttr();
			skuSaleAttr.setAttrId(m.getAttrId());
			skuSaleAttr.setAttrValue(m.getAttrValue());
			skuSaleAttr.setAttrName(m.getAttrName());

			if (spuAttrs.containsKey(m.getSkuId()))
			{
				List<SkuItemVo.SkuSaleAttr> skuSaleAttrs = spuAttrs.get(m.getSkuId());
				skuSaleAttrs.add(skuSaleAttr);

			}else
			{
				List<SkuItemVo.SkuSaleAttr> skuSaleAttrs=new ArrayList<>();
				skuSaleAttrs.add(skuSaleAttr);
				spuAttrs.put(m.getSkuId(),skuSaleAttrs);
			}
		});
		return spuAttrs;
	}

}