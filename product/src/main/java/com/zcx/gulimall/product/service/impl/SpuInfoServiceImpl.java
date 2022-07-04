package com.zcx.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.common.to.MemberPrice;
import com.zcx.common.to.SkuReductionTo;
import com.zcx.common.to.SpuBoundTo;
import com.zcx.gulimall.product.entity.*;
import com.zcx.gulimall.product.feign.CouponFeignService;
import com.zcx.gulimall.product.service.*;
import com.zcx.gulimall.product.vo.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.Query;

import com.zcx.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService
{

	@Autowired
	private SpuInfoDescService spuInfoDesc;
	@Autowired
	private SpuImagesService spuImagesService;

	@Autowired
	private ProductAttrValueService productAttrValueS;

	@Autowired
	SkuImagesService skuImagesService;

	@Autowired
	SkuInfoService skuInfoService;
	@Autowired
	SkuSaleAttrValueService skuSaleAttrValueService;


	@Autowired
	CouponFeignService couponFeignService;

	@Override
	public PageUtils queryPage(Map<String, Object> params)
	{
		IPage<SpuInfoEntity> page = this.page(
				new Query<SpuInfoEntity>().getPage(params),
				new QueryWrapper<SpuInfoEntity>()
		);

		return new PageUtils(page);
	}

	@Transactional
	@Override
	public void saveSpuInfo(SpuSaveVo spuInfo)
	{
		//spu信息
		//spu基本信息 spu_info
		SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
		BeanUtils.copyProperties(spuInfo, spuInfoEntity);
		save(spuInfoEntity);


		//spu描述信息 spu_info_desc
		SpuInfoDescEntity entity = new SpuInfoDescEntity();
		entity.setSpuId(spuInfoEntity.getId());
		List<String> decript = spuInfo.getDecript();
		entity.setDecript(String.join(",", decript));
		spuInfoDesc.save(entity);


		//spu图片信息spu_images
		spuImagesService.saveImages(spuInfoEntity.getId(), spuInfo.getImages());


		//spu规格参数product_attr_value
		List<BaseAttrs> baseAttrs = spuInfo.getBaseAttrs();
		productAttrValueS.saveRelation(spuInfoEntity.getId(), baseAttrs);


		//spu积分信息gulimall_sms/spu_bounds
		Bounds bounds = spuInfo.getBounds();
		SpuBoundTo spuBoundTo = new SpuBoundTo();
		BeanUtils.copyProperties(bounds, spuBoundTo);
		spuBoundTo.setSpuId(spuInfoEntity.getId());
		couponFeignService.saveSpuBounds(spuBoundTo);


		//spu对应的所有sku信息
		//sku基本信息sku_info
		List<Skus> skus = spuInfo.getSkus();
		if (skus != null && !skus.isEmpty()) {
			skus.forEach(item -> {
				SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
				for (Images image : item.getImages()) {
					if (image.getDefaultImg() == 1) {
						skuInfoEntity.setSkuDefaultImg(image.getImgUrl());
					}
				}
				BeanUtils.copyProperties(item, skuInfoEntity);
				skuInfoEntity.setSpuId(spuInfoEntity.getId());
				skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
				skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
				skuInfoEntity.setSaleCount(0L);
				skuInfoService.save(skuInfoEntity);

				//sku图片信息sku_images
				List<SkuImagesEntity> collect = item.getImages().stream().map(images ->
				{

					if (Strings.isNotEmpty(images.getImgUrl())) {
						SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
						BeanUtils.copyProperties(images, skuImagesEntity);
						skuImagesEntity.setSkuId(skuInfoEntity.getSkuId());
						return skuImagesEntity;
					}
					return null;

				}).filter(Objects::nonNull).collect(Collectors.toList());
				skuImagesService.saveBatch(collect);


				//sku销售属性信息sku_sale_attr_value
				List<Attr> attrs = item.getAttr();
				List<SkuSaleAttrValueEntity> collect1 = attrs.stream().map(attr -> {
					SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
					BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
					skuSaleAttrValueEntity.setSkuId(skuInfoEntity.getSkuId());
					return skuSaleAttrValueEntity;
				}).collect(Collectors.toList());
				skuSaleAttrValueService.saveBatch(collect1);

				//sku优惠满减信息gulimall_sms  /sku_Ladder/full_reduction/member_price
				SkuReductionTo skuReductionTo = new SkuReductionTo();
				BeanUtils.copyProperties(item, skuReductionTo);
				skuReductionTo.setSkuId(skuInfoEntity.getSkuId());

				List<MemberPrice> memberPrice = item.getMemberPrice().stream().filter(price ->
						price.getPrice().compareTo(BigDecimal.valueOf(0)) > 0
				).collect(Collectors.toList());
				skuReductionTo.setMemberPrice(memberPrice);

				if (skuReductionTo.getFullCount()>0||skuReductionTo.getReducePrice().compareTo(BigDecimal.valueOf(0))>0||!memberPrice.isEmpty())
				couponFeignService.saveSkuReduction(skuReductionTo);


			});
		}


	}

	@Override
	public PageUtils queryPageByCondition(Map<String, Object> params)
	{
		LambdaQueryWrapper<SpuInfoEntity> wrapper = new LambdaQueryWrapper<>();
		String brandId = (String)params.get("brandId");
		String catelogId =(String)params.get("catelogId");
		String status = (String)params.get("status");
		String key=(String)params.get("key");



		wrapper.eq(Objects.nonNull(brandId),SpuInfoEntity::getBrandId,Long.valueOf(brandId));
		wrapper.eq(Objects.nonNull(catelogId),SpuInfoEntity::getCatalogId,Long.valueOf(catelogId));
		wrapper.eq(Strings.isNotEmpty(status),SpuInfoEntity::getPublishStatus,Integer.valueOf(status));
		wrapper.and(Strings.isNotEmpty(key),w->{
			w.like(SpuInfoEntity::getSpuName,key).or().like(SpuInfoEntity::getSpuDescription,key);
		});


		IPage<SpuInfoEntity> page = this.page(
				new Query<SpuInfoEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}
}