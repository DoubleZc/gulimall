package com.zcx.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.zcx.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.zcx.gulimall.product.entity.AttrEntity;
import com.zcx.gulimall.product.service.CategoryService;
import com.zcx.gulimall.product.vo.AttrGroupVo;
import com.zcx.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zcx.gulimall.product.entity.AttrGroupEntity;
import com.zcx.gulimall.product.service.AttrGroupService;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.R;


/**
 * 属性分组
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-20 18:41:22
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController
{
@Autowired
private AttrGroupService attrGroupService;
@Autowired
private CategoryService categoryService;

/**
 * 列表
 */
@RequestMapping("/list/{catelogId}")
//  @RequiresPermissions("product:attrgroup:list")
public R list(@RequestParam Map<String, Object> params,
              @PathVariable Long catelogId)
{
	PageUtils page = attrGroupService.queryPage(params, catelogId);
	return R.ok().put("page", page);
}


@GetMapping("/{attrgroupId}/attr/relation")

public R attrlist(
		@PathVariable Long attrgroupId)
{
	List<AttrEntity> entities = attrGroupService.getAttrByGroup(attrgroupId);
	return R.ok().put("data",entities);
}


/**
 * 信息
 */
@RequestMapping("/info/{attrGroupId}")
//@RequiresPermissions("product:attrgroup:info")
public R info(@PathVariable("attrGroupId") Long attrGroupId)
{
	AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
	Long[] path = categoryService.findPath(attrGroup.getCatelogId());
	attrGroup.setCatelogPath(path);
	return R.ok().put("attrGroup", attrGroup);
}

/***
 * /product/attrgroup/{attrgroupId}/noattr/relation
 * 查询没有关联的attr
 * group与attr的catelogid相同 并且attr不在relation表中
 */
@GetMapping("/{attrgroupId}/noattr/relation")
public R getNoCAttr(@RequestParam Map<String, Object> params,
                     @PathVariable Long attrgroupId)
{
	PageUtils page=attrGroupService.getNoCAttr(params,attrgroupId);
	return R.ok().put("page",page);
}


	/***
	 * 查找catid下所有的group并携带所有attr的信息
	 *product/attrgroup/{catelogId}/withattr
	 *
	 *
	 */
	@GetMapping("/{catelogId}/withattr")
	public R getGroupAttr(@PathVariable Long catelogId)
	{
		List<AttrGroupVo> attrGroupVo = attrGroupService.getGroupDetail(catelogId);
		return R.ok().put("data",attrGroupVo);

	}




/***
 *
 * /product/attrgroup/attr/relation
 * 添加relation
 */
@RequestMapping("/attr/relation")
//@RequiresPermissions("product:attrgroup:delete")
public R saveRelastion(@RequestBody List<AttrAttrgroupRelationEntity> entities)
{
	attrGroupService.saveRelation(entities);
	return R.ok();
}





/**
 * 保存
 */
@RequestMapping("/save")
//    @RequiresPermissions("product:attrgroup:save")
public R save(@RequestBody AttrGroupEntity attrGroup)
{
	attrGroupService.save(attrGroup);
	return R.ok();
}

/**
 * 修改
 */
@RequestMapping("/update")
//  @RequiresPermissions("product:attrgroup:update")
public R update(@RequestBody AttrGroupEntity attrGroup)
{
	attrGroupService.updateById(attrGroup);

	return R.ok();
}
/**
 * 删除relastion
 * /product/attrgroup/attr/relation/delete
 */
@RequestMapping("/attr/relation/delete")
//@RequiresPermissions("product:attrgroup:delete")
public R deleteRelastion(@RequestBody List<AttrAttrgroupRelationEntity> entities)
{
	attrGroupService.removeRelation(entities);
	return R.ok();
}
/**
 * 删除
 * /
 */
@RequestMapping("/delete")
//@RequiresPermissions("product:attrgroup:delete")
public R delete(@RequestBody Long[] attrGroupIds)
{
	attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

	return R.ok();
}

}
