package com.zcx.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.gulimall.ware.entity.PurchaseDetailEntity;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.Query;

import com.zcx.gulimall.ware.dao.PurchaseDetailDao;
import com.zcx.gulimall.ware.entity.PurchaseDetailEntity;
import com.zcx.gulimall.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                new QueryWrapper<PurchaseDetailEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public PageUtils queryPageByCondition(Map<String, Object> params)
	{
		LambdaQueryWrapper<PurchaseDetailEntity> wrapper = new LambdaQueryWrapper<>();
		String key = (String) params.get("key");
		String wareId= (String) params.get("wareId");
		String status= (String) params.get("status");

		wrapper.and(Strings.isNotEmpty(key), w->{
			w.eq(PurchaseDetailEntity::getPurchaseId,key).or().eq(PurchaseDetailEntity::getId,key);
		});
		wrapper.eq(Strings.isNotEmpty(wareId),PurchaseDetailEntity::getWareId,wareId);
		wrapper.eq(Strings.isNotEmpty(status),PurchaseDetailEntity::getStatus,status);


		IPage<PurchaseDetailEntity> page = this.page(
				new Query<PurchaseDetailEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}

	@Override
	public List<PurchaseDetailEntity> listDetailByPurch(Long id)
	{
		LambdaQueryWrapper<PurchaseDetailEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(PurchaseDetailEntity::getPurchaseId,id);
		return list(wrapper);
	}

}