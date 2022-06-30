package com.zcx.gulimall.product.service.impl;

import com.alibaba.nacos.api.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Consumer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.Query;

import com.zcx.gulimall.product.dao.AttrGroupDao;
import com.zcx.gulimall.product.entity.AttrGroupEntity;
import com.zcx.gulimall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {

//        if (catelogId == 0) {
//            return queryPage(params);
//        } else {
            String key = (String) params.get("key");
            LambdaQueryWrapper<AttrGroupEntity> lambdaQueryWrapper = new LambdaQueryWrapper<AttrGroupEntity>().eq(catelogId!=0,AttrGroupEntity::getCatelogId, catelogId);
            lambdaQueryWrapper.and(Strings.isNotEmpty(key), wrapper ->
                    {
                        wrapper.like(AttrGroupEntity::getAttrGroupId, key).or().like(AttrGroupEntity::getAttrGroupName, key).or().like(AttrGroupEntity::getDescript, key);
                    }
            );
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), lambdaQueryWrapper);
            return new PageUtils(page);
     //   }
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

}