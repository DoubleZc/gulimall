package com.zcx.gulimall.product.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.common.utils.PageUtils;
import com.zcx.gulimall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-20 18:00:35
 */
public interface BrandService extends IService<BrandEntity> {
    PageUtils queryPage(Map<String, Object> params, Wrapper<BrandEntity> wrapper);
    PageUtils findPage(Map<String, Object> params);
    void updateDetail(BrandEntity brand);
}

