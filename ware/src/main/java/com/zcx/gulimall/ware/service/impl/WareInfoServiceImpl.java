package com.zcx.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.Query;

import com.zcx.gulimall.ware.dao.WareInfoDao;
import com.zcx.gulimall.ware.entity.WareInfoEntity;
import com.zcx.gulimall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                new QueryWrapper<WareInfoEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public PageUtils queryPageBykey(Map<String, Object> params)
	{
		LambdaQueryWrapper<WareInfoEntity> wrapper = new LambdaQueryWrapper<>();
		String key = (String) params.get("key");
		wrapper.and(Strings.isNotEmpty(key),w->{
			w.like(WareInfoEntity::getName,key).or().like(WareInfoEntity::getAddress,key).or().eq(WareInfoEntity::getAreacode,key);
		});

		IPage<WareInfoEntity> page = this.page(
				new Query<WareInfoEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);




	}

}