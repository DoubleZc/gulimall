package com.zcx.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.gulimall.ware.entity.WareSkuEntity;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.Query;

import com.zcx.gulimall.ware.dao.WareSkuDao;
import com.zcx.gulimall.ware.entity.WareSkuEntity;
import com.zcx.gulimall.ware.service.WareSkuService;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

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

}