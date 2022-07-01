package com.zcx.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.Query;
import com.zcx.gulimall.product.dao.BrandDao;
import com.zcx.gulimall.product.entity.BrandEntity;
import com.zcx.gulimall.product.entity.CategoryBrandRelationEntity;
import com.zcx.gulimall.product.service.BrandService;
import com.zcx.gulimall.product.service.CategoryBrandRelationService;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.weaver.IClassWeaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {


    @Autowired
    @Lazy
    private CategoryBrandRelationService categoryBrandRelationService;
    @Override
    @Transactional
    public void updateDetail(BrandEntity brand) {
        updateById(brand);
        categoryBrandRelationService.updateBrand(brand.getBrandId(),brand.getName());

    }





    @Override
    public PageUtils findPage(Map<String, Object> params) {
        String key = (String) params.get("key");
        if (Strings.isNotEmpty(key)) {
            LambdaQueryWrapper<BrandEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(BrandEntity::getBrandId, key).or().like(BrandEntity::getName, key).or().like(BrandEntity::getDescript, key).or().like(BrandEntity::getFirstLetter, key);
            wrapper.orderByAsc(BrandEntity::getSort);
            return new PageUtils(this.page(new Query<BrandEntity>().getPage(params), wrapper));

        } else {

            LambdaQueryWrapper<BrandEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.orderByAsc(BrandEntity::getSort);
            return queryPage(params,wrapper);
        }


    }

    @Override
    public PageUtils queryPage(Map<String, Object> params,Wrapper<BrandEntity> wrapper) {
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                wrapper);
        return new PageUtils(page);
    }

}