package com.zcx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.common.utils.PageUtils;
import com.zcx.gulimall.product.entity.AttrEntity;
import com.zcx.gulimall.product.entity.AttrGroupEntity;
import com.zcx.gulimall.product.vo.AttrRespVo;
import com.zcx.gulimall.product.vo.AttrVo;

import java.util.Map;

/**
 * 商品属性
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-20 18:00:34
 */
public interface AttrService extends IService<AttrEntity>
{

AttrGroupEntity getGroupByAttr(Long attrId);

void saveAttr(AttrVo attr);

PageUtils queryBasePage(Map<String, Object> params, Long catelogId, String type);

AttrRespVo attrInfo(Long attrId);

void updateAttr(AttrVo attr);
}

