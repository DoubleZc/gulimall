package com.zcx.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.common.constant.ProductConstant;
import com.zcx.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.zcx.gulimall.product.entity.AttrEntity;
import com.zcx.gulimall.product.service.AttrAttrgroupRelationService;
import com.zcx.gulimall.product.service.AttrService;
import com.zcx.gulimall.product.vo.AttrGroupVo;
import com.zcx.gulimall.product.vo.AttrRespVo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.Query;

import com.zcx.gulimall.product.dao.AttrGroupDao;
import com.zcx.gulimall.product.entity.AttrGroupEntity;
import com.zcx.gulimall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService
{
	@Autowired
	@Lazy
	private AttrService attrService;

	@Autowired
	AttrAttrgroupRelationService relationService;


	@Override
	public List<AttrEntity> getAttrByGroup(Long attrgroupId)
	{
		LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupId);
		List<AttrAttrgroupRelationEntity> entities = relationService.list(wrapper);
		List<Long> longs = entities.stream().map(AttrAttrgroupRelationEntity::getAttrId
		).collect(Collectors.toList());
		if (longs.isEmpty())
			return null;
		return attrService.listByIds(longs);
	}


	@Override
	public void saveRelation(List<AttrAttrgroupRelationEntity> entities)
	{

		entities.forEach(item ->

				{
					LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaQueryWrapper<>();
					wrapper.eq(AttrAttrgroupRelationEntity::getAttrId, item.getAttrId());
					relationService.saveOrUpdate(item, wrapper);
				}
		);

	}

	@Override
	public List<AttrGroupVo> getGroupDetail(Long catelogId)
	{
		List<AttrGroupEntity> list = list(new LambdaQueryWrapper<AttrGroupEntity>().eq(AttrGroupEntity::getCatelogId, catelogId));


		List<AttrGroupVo> collect = list.stream().map(item -> {
			AttrGroupVo attrGroupVo = new AttrGroupVo();
			BeanUtils.copyProperties(item, attrGroupVo);
			List<AttrEntity> attrByGroup = getAttrByGroup(item.getAttrGroupId());
			attrGroupVo.setAttrs(attrByGroup);
			return attrGroupVo;
		}).collect(Collectors.toList());
		return collect;

	}

	@Override
	public void removeRelation(List<AttrAttrgroupRelationEntity> entities)
	{
		entities.forEach(temp -> {
					Map<String, Object> map = new HashMap<>();
					map.put("attr_id", temp.getAttrId());
					map.put("attr_group_id", temp.getAttrGroupId());
					relationService.removeByMap(map);
				}
		);
	}

	@Override
	public PageUtils getNoCAttr(Map<String, Object> params, Long attrgroupId)
	{
		AttrGroupEntity entity = getById(attrgroupId);
		Long catelogId = entity.getCatelogId();
		PageUtils page = attrService.queryBasePage(params, catelogId, ProductConstant.AttrType.BASE.getMsg());
		List<AttrRespVo> list = (List<AttrRespVo>) page.getList();
		List<AttrRespVo> collect = list.stream().filter(item -> {
			return item.getGroupName() == null;
		}).collect(Collectors.toList());
		page.setList(collect);
		return page;

	}

	@Override
	public PageUtils queryPage(Map<String, Object> params, Long catelogId)
	{

		String key = (String) params.get("key");
		LambdaQueryWrapper<AttrGroupEntity> lambdaQueryWrapper = new LambdaQueryWrapper<AttrGroupEntity>().eq(catelogId != 0, AttrGroupEntity::getCatelogId, catelogId);
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
	public PageUtils queryPage(Map<String, Object> params)
	{
		IPage<AttrGroupEntity> page = this.page(
				new Query<AttrGroupEntity>().getPage(params),
				new QueryWrapper<AttrGroupEntity>()
		);

		return new PageUtils(page);
	}

}