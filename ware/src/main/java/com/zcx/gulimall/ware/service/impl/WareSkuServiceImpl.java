package com.zcx.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.common.utils.R;
import com.zcx.gulimall.ware.entity.PurchaseDetailEntity;
import com.zcx.gulimall.ware.entity.WareSkuEntity;
import com.zcx.gulimall.ware.feign.ProductFeign;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.Query;

import com.zcx.gulimall.ware.dao.WareSkuDao;
import com.zcx.gulimall.ware.service.WareSkuService;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

	@Autowired
	ProductFeign productFeign;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public PageUtils queryPageByCondition(Map<String, Object> params)
	{
		LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();
		String skuId= (String) params.get("skuId");
		String wareId= (String) params.get("wareId");
		wrapper.eq(Strings.isNotEmpty(skuId),WareSkuEntity::getSkuId,skuId);
		wrapper.eq(Strings.isNotEmpty(wareId),WareSkuEntity::getWareId,wareId);


		IPage<WareSkuEntity> page = this.page(
				new Query<WareSkuEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);


	}

	@Override
	public void saveWareSku(List<PurchaseDetailEntity> entities)
	{
		entities.forEach(entity -> {
			Long skuId = entity.getSkuId();
			Long wareId = entity.getWareId();
			Integer skuNum = entity.getSkuNum();
			R one = productFeign.getById(skuId);
			String name = (String) one.get("name");
			LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();
			wrapper.eq(WareSkuEntity::getSkuId,skuId).eq(WareSkuEntity::getWareId,wareId);
			WareSkuEntity skuEntity = getOne(wrapper);
			if (skuEntity==null)
			{
				WareSkuEntity wareSkuEntity = new WareSkuEntity();
				wareSkuEntity.setSkuName(name);
				wareSkuEntity.setSkuId(skuId);
				wareSkuEntity.setWareId(wareId);
				wareSkuEntity.setStock(skuNum);
				save(wareSkuEntity);
			}else
			{
				Integer stock = skuEntity.getStock();
				stock +=skuNum;
				Long id = skuEntity.getId();
				WareSkuEntity entity1 = new WareSkuEntity();
				entity1.setId(id);
				entity1.setStock(stock);
				updateById(entity1);
			}
		});

	}

	@Override
	public Integer getBySkuId(Long skuId)
	{
		QueryWrapper<WareSkuEntity> wrapper=new QueryWrapper<>();
		wrapper.eq("sku_id",skuId).select("sum(stock-stock_locked) as sumStock");
		WareSkuEntity one = getOne(wrapper);
		if (one!=null)
		return one.getSumStock();
		else
			return  0;
	}

}