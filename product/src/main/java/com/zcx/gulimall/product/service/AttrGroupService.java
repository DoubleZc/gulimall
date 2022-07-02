package com.zcx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.common.utils.PageUtils;
import com.zcx.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.zcx.gulimall.product.entity.AttrEntity;
import com.zcx.gulimall.product.entity.AttrGroupEntity;
import com.zcx.gulimall.product.vo.AttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-20 18:00:35
 */
public interface AttrGroupService extends IService<AttrGroupEntity>
{

	PageUtils queryPage(Map<String, Object> params);

	PageUtils queryPage(Map<String, Object> params, Long catelogId);

	List<AttrEntity> getAttrByGroup(Long attrgroupId);

	void removeRelation(List<AttrAttrgroupRelationEntity> entities);

	PageUtils getNoCAttr(Map<String, Object> params, Long attrgroupId);

	void saveRelation(List<AttrAttrgroupRelationEntity> entities);

	List<AttrGroupVo> getGroupDetail(Long catelogId);
}

