package com.zcx.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.constant.ProductConstant;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.Query;
import com.zcx.gulimall.product.dao.AttrDao;
import com.zcx.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.zcx.gulimall.product.entity.AttrEntity;
import com.zcx.gulimall.product.entity.AttrGroupEntity;
import com.zcx.gulimall.product.entity.CategoryEntity;
import com.zcx.gulimall.product.service.AttrAttrgroupRelationService;
import com.zcx.gulimall.product.service.AttrGroupService;
import com.zcx.gulimall.product.service.AttrService;
import com.zcx.gulimall.product.service.CategoryService;
import com.zcx.gulimall.product.vo.AttrRespVo;
import com.zcx.gulimall.product.vo.AttrVo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService
{


@Autowired
private AttrAttrgroupRelationService relationService;

@Autowired
private CategoryService categoryService;
@Autowired
private AttrGroupService attrGroupService;

@Override
public AttrRespVo attrInfo(Long attrId)
{
	//拷贝基本数据
	AttrRespVo attrRespVo = new AttrRespVo();
	AttrEntity attrEntity = getById(attrId);
	BeanUtils.copyProperties(attrEntity, attrRespVo);

	//设置路径信息
	Long catelogId = attrEntity.getCatelogId();
	Long[] path = categoryService.findPath(catelogId);
	CategoryEntity byId = categoryService.getById(catelogId);
	attrRespVo.setCatelogName(byId.getName());
	attrRespVo.setCatelogPath(path);

	if (ProductConstant.AttrType.BASE.getCode().equals(attrEntity.getAttrType())) {
		//设置组id信息
		AttrGroupEntity groupByAttr = getGroupByAttr(attrId);
		if (groupByAttr != null) {
			attrRespVo.setAttrGroupId(groupByAttr.getAttrGroupId());
			attrRespVo.setGroupName(groupByAttr.getAttrGroupName());
		}
	}
	return attrRespVo;
}

@Override
public void updateAttr(AttrVo attr)
{
	AttrEntity attrEntity = new AttrEntity();
	BeanUtils.copyProperties(attr, attrEntity);
	//      更新基本数据
	updateById(attrEntity);

	if (ProductConstant.AttrType.BASE.getCode().equals(attr.getAttrType())) {
		//        更新中间表
		LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId());
		AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
		attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
		//        判断中间表是否有关联关系
		if (relationService.getOne(wrapper) != null)
			//       有，更新操作
			relationService.update(attrAttrgroupRelationEntity, wrapper);

		//        无，插入操作
		attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
		relationService.save(attrAttrgroupRelationEntity);
	}
}

@Override
public PageUtils queryBasePage(Map<String, Object> params, Long catelogId, String type)
{
	String key = (String) params.get("key");
	LambdaQueryWrapper<AttrEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
	lambdaQueryWrapper.eq(AttrEntity::getAttrType, ProductConstant.AttrType.BASE.getMsg().equals(type) ? 1 : 0);
	lambdaQueryWrapper.eq(catelogId != 0, AttrEntity::getCatelogId, catelogId);
	lambdaQueryWrapper.and(Strings.isNotEmpty(key), wrapper ->
			wrapper.like(AttrEntity::getAttrName, key).or().like(AttrEntity::getAttrId, key)
	);
	IPage<AttrEntity> page = page(new Query<AttrEntity>().getPage(params), lambdaQueryWrapper);
	List<AttrEntity> records = page.getRecords();

	//封装AttrRespVo，添加组名和标签名
	List<AttrRespVo> list = records.stream().map(attrEntity -> {

		AttrRespVo attrRespVo = new AttrRespVo();
		BeanUtils.copyProperties(attrEntity, attrRespVo);

		CategoryEntity categoryEntity = categoryService.getById(attrEntity.getCatelogId());
		String categoryEntityName = categoryEntity.getName();
		attrRespVo.setCatelogName(categoryEntityName);
		if (ProductConstant.AttrType.BASE.getMsg().equals(type)) {
			AttrGroupEntity groupByAttr = getGroupByAttr(attrEntity.getAttrId());
			if (groupByAttr != null) {
				attrRespVo.setGroupName(groupByAttr.getAttrGroupName());
			}
		}

		return attrRespVo;
	}).collect(Collectors.toList());
	PageUtils pageUtils = new PageUtils(page);
	pageUtils.setList(list);
	return pageUtils;


}

/***
 * 用成员Id类拿组类
 */
@Override
public AttrGroupEntity getGroupByAttr(Long attrId)
{
	AttrAttrgroupRelationEntity one = relationService.getOne(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().eq(AttrAttrgroupRelationEntity::getAttrId, attrId));
	if (one != null) {
		Long attrGroupId = one.getAttrGroupId();
		if (attrGroupId != null) {
			return attrGroupService.getById(attrGroupId);
		}
	}
	return null;
}


	@Override
	public List<AttrGroupEntity> listGroupByAttr(List<Long> Ids)
	{
		LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.in(AttrAttrgroupRelationEntity::getAttrId,Ids);
		List<AttrAttrgroupRelationEntity> list = relationService.list(wrapper);
		List<Long> groupIds = list.stream().map(AttrAttrgroupRelationEntity::getAttrGroupId).collect(Collectors.toList());

		if (!groupIds.isEmpty()) {

			return attrGroupService.list(new LambdaQueryWrapper<AttrGroupEntity>().in(AttrGroupEntity::getAttrGroupId, groupIds));
		}
		return null;
	}






@Transactional
@Override
public void saveAttr(AttrVo attr)
{
	save(attr);
	if (ProductConstant.AttrType.BASE.getCode().equals(attr.getAttrType())) {
		AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
		attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
		attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
		relationService.save(attrAttrgroupRelationEntity);
	}
}

}