package com.zcx.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.gulimall.product.entity.SkuImagesEntity;
import com.zcx.gulimall.product.entity.SkuInfoEntity;
import com.zcx.gulimall.product.entity.SpuInfoDescEntity;
import com.zcx.gulimall.product.service.*;
import com.zcx.gulimall.product.vo.SkuItemVo;
import com.zcx.gulimall.product.vo.SpuSaveVo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.Query;

import com.zcx.gulimall.product.dao.SkuInfoDao;
import com.zcx.gulimall.product.entity.SkuInfoEntity;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {



	@Autowired
	SkuImagesService skuImagesService;

	@Autowired
	SpuInfoDescService spuInfoDescService;


	@Autowired
	SkuSaleAttrValueService skuSaleAttrValueService;


	@Autowired
	ProductAttrValueService productAttrValueService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public PageUtils queryPageByCondition(Map<String, Object> params)
	{
		LambdaQueryWrapper<SkuInfoEntity> wrapper = new LambdaQueryWrapper<>();
		String brandId = (String)params.get("brandId");
		String catelogId =(String)params.get("catelogId");
		String key=(String)params.get("key");
		Integer min = Integer.valueOf((String) params.get("min"));
		Integer max = Integer.valueOf((String) params.get("max"));
		wrapper.eq(!"0".equals(brandId),SkuInfoEntity::getBrandId,Long.valueOf(brandId));
		wrapper.eq(!"0".equals(brandId),SkuInfoEntity::getCatalogId,Long.valueOf(catelogId));
		wrapper.and(Strings.isNotEmpty(key),w->{
			w.like(SkuInfoEntity::getSkuName,key).or().like(SkuInfoEntity::getSkuDesc,key);
		});
		wrapper.ge(min!=0,SkuInfoEntity::getPrice,min);
		wrapper.le(max!=0,SkuInfoEntity::getPrice,max);





		IPage<SkuInfoEntity> page = this.page(
				new Query<SkuInfoEntity>().getPage(params),

				wrapper

		);

		return new PageUtils(page);
	}

	@Override
	public List<SkuInfoEntity> getBySpuId(Long spuId)
	{

		return list(new LambdaQueryWrapper<SkuInfoEntity>().eq(SkuInfoEntity::getSpuId, spuId));
	}

	@Override
	public SkuItemVo item(Long skuId)
	{
		SkuItemVo skuItemVo = new SkuItemVo();
		SkuInfoEntity infoEntity = getById(skuId);
		skuItemVo.setInfo(infoEntity);
		List<SkuImagesEntity> list = skuImagesService.listBySkuId(skuId);
		skuItemVo.setImages(list);
		Long spuId = infoEntity.getSpuId();

		List<SkuInfoEntity> skuInfoEntities = list(new LambdaQueryWrapper<SkuInfoEntity>().eq(SkuInfoEntity::getSpuId, spuId));
		List<Long> collect = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());


		SpuInfoDescEntity one = spuInfoDescService.getBySpuId(spuId);
		skuItemVo.setDesc(one);


		List<SkuItemVo.SpuItemBaseAttr>spuItemBaseAttr=productAttrValueService.listBySpuId(spuId);
		skuItemVo.setGroupAttrs(spuItemBaseAttr);



		List<SkuItemVo.SkuSaleAttr> skuSaleAttrs=skuSaleAttrValueService.listBySkuId(skuId);
		skuItemVo.setSkuSaleAttrs(skuSaleAttrs);


		Map<Long,List<SkuItemVo.SkuSaleAttr>> spuAttrs=skuSaleAttrValueService.MapBySpuId(collect);
		skuItemVo.setSpuAttrs(spuAttrs);


		return skuItemVo;

	}


}