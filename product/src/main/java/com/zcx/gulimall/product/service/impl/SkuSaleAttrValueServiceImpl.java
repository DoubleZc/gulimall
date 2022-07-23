package com.zcx.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.gulimall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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

	@Autowired
	SkuSaleAttrValueDao skuSaleAttrValueDao;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public List<SkuItemVo.SkuSaleAttr> listBySkuId(List<SkuItemVo.SkuSaleAttr> attrRSkuId, Long skuId)
	{
		List<SkuSaleAttrValueEntity> list = list(new LambdaQueryWrapper<SkuSaleAttrValueEntity>().eq(SkuSaleAttrValueEntity::getSkuId, skuId));
		 list.forEach(t -> {
			attrRSkuId.forEach(i->{
				if (i.getAttrId().equals(t.getAttrId()))
				{
					i.setAttrValue(t.getAttrValue());
				}
			});
		});
		return attrRSkuId;
	}

	@Override
	public Map<String, Long> MapBySpuId(List<Long> collect)
	{
		QueryWrapper<SkuSaleAttrValueEntity> wrapper = new QueryWrapper<SkuSaleAttrValueEntity>().in("sku_id", collect).groupBy("sku_id");
		wrapper.select("sku_id,GROUP_CONCAT(attr_id,'_',attr_value) as attr");
		List<SkuSaleAttrValueEntity> list = list(wrapper);

		Map<String, Long> collect1 = list.stream().collect(Collectors.toMap(SkuSaleAttrValueEntity::getAttr, SkuSaleAttrValueEntity::getSkuId));


//		Map<Long, List<SkuItemVo.SkuSaleAttr>> spuAttrs=new HashMap<>();
//		list.forEach(m->{
//			SkuItemVo.SkuSaleAttr skuSaleAttr = new SkuItemVo.SkuSaleAttr();
//			skuSaleAttr.setAttrId(m.getAttrId());
//			skuSaleAttr.setAttrValue(m.getAttrValue());
//			skuSaleAttr.setAttrName(m.getAttrName());
//
//			if (spuAttrs.containsKey(m.getSkuId()))
//			{
//				List<SkuItemVo.SkuSaleAttr> skuSaleAttrs = spuAttrs.get(m.getSkuId());
//				skuSaleAttrs.add(skuSaleAttr);
//
//			}else
//			{
//				List<SkuItemVo.SkuSaleAttr> skuSaleAttrs=new ArrayList<>();
//				skuSaleAttrs.add(skuSaleAttr);
//				spuAttrs.put(m.getSkuId(),skuSaleAttrs);
//			}
//		});




		return collect1;
	}

	@Override
	public List<SkuItemVo.SkuSaleAttr> attrRSkuId(Long spuId)
	{

		List<SkuItemVo.SkuSaleAttr> skuSaleAttrs = skuSaleAttrValueDao.attrRSkuId(spuId);
		skuSaleAttrs.forEach(i->{
			String[] split = i.getAttrValues().split(",");
			List<SkuItemVo.attrRSkuId>skuIds=skuSaleAttrValueDao.skuIds(Arrays.asList(split));
			i.setAttrRSkuIds(skuIds);
		});
		return skuSaleAttrs;
	}

}