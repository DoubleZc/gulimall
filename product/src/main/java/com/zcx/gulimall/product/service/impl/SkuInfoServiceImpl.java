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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.Query;

import com.zcx.gulimall.product.dao.SkuInfoDao;
import com.zcx.gulimall.product.entity.SkuInfoEntity;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService
{


	@Autowired
	SkuImagesService skuImagesService;

	@Autowired
	SpuInfoDescService spuInfoDescService;


	@Autowired
	SkuSaleAttrValueService skuSaleAttrValueService;


	@Autowired
	ProductAttrValueService productAttrValueService;


	@Autowired
	ThreadPoolExecutor threadPoolExecutor;

	@Override
	public PageUtils queryPage(Map<String, Object> params)
	{
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
		String brandId = (String) params.get("brandId");
		String catelogId = (String) params.get("catelogId");
		String key = (String) params.get("key");
		int min = Integer.parseInt((String) params.get("min"));
		int max = Integer.parseInt((String) params.get("max"));
		wrapper.eq(!"0".equals(brandId), SkuInfoEntity::getBrandId, Long.valueOf(brandId));
		wrapper.eq(!"0".equals(brandId), SkuInfoEntity::getCatalogId, Long.valueOf(catelogId));
		wrapper.and(Strings.isNotEmpty(key), w -> {
			w.like(SkuInfoEntity::getSkuName, key).or().like(SkuInfoEntity::getSkuDesc, key);
		});
		wrapper.ge(min != 0, SkuInfoEntity::getPrice, min);
		wrapper.le(max != 0, SkuInfoEntity::getPrice, max);


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



	@Cacheable(value = {"product"},key = "#skuId")
	@Override
	public SkuItemVo item(Long skuId)
	{
		SkuItemVo skuItemVo = new SkuItemVo();
		CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
			//获取图片信息
			List<SkuImagesEntity> list = skuImagesService.listBySkuId(skuId);
			skuItemVo.setImages(list);
		}, threadPoolExecutor);

		CompletableFuture<SkuInfoEntity> future = CompletableFuture.supplyAsync(() -> {
			//获取基本信息
			SkuInfoEntity infoEntity = getById(skuId);
			skuItemVo.setInfo(infoEntity);
			return infoEntity;
		}, threadPoolExecutor);
		//future后续
		CompletableFuture<Void> future2 = future.thenAcceptAsync(skuInfoEntity -> {
			//获取描述图片
			SpuInfoDescEntity one = spuInfoDescService.getBySpuId(skuInfoEntity.getSpuId());
			skuItemVo.setDesc(one);
		}, threadPoolExecutor);
		//future后续
		CompletableFuture<Void> future3 = future.thenAcceptAsync(skuInfoEntity -> {
			//获取属性组信息
			List<SkuItemVo.SpuItemBaseAttr> spuItemBaseAttr = productAttrValueService.listBySpuId(skuInfoEntity.getSpuId());
			skuItemVo.setGroupAttrs(spuItemBaseAttr);
		}, threadPoolExecutor);

		//future后续
		CompletableFuture<List<SkuItemVo.SkuSaleAttr>> listCompletableFuture = future.thenApplyAsync(skuInfoEntity -> skuSaleAttrValueService.attrRSkuId(skuInfoEntity.getSpuId()), threadPoolExecutor);
		//listCompletableFuture后续
		CompletableFuture<Void> future4 = listCompletableFuture.thenAcceptAsync(skuSaleAttrs -> {
			//获取属性信息
			List<SkuItemVo.SkuSaleAttr> saleAttrs = skuSaleAttrValueService.listBySkuId(skuSaleAttrs, skuId);
			skuItemVo.setSkuSaleAttrs(saleAttrs);
		});
		CompletableFuture<Void> futureAll = CompletableFuture.allOf(future1,future2,future3,future4);

		try {
			futureAll.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return skuItemVo;
	}


}