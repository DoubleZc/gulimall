package com.zcx.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zcx.gulimall.product.entity.CategoryBrandRelationEntity;
import com.zcx.gulimall.product.service.CategoryBrandRelationService;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.R;


/**
 * 品牌分类关联
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-20 18:41:22
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController
{
	@Autowired
	private CategoryBrandRelationService categoryBrandRelationService;


	/**
	 * 列表
	 */
	@GetMapping("/catelog/list")
	//  @RequiresPermissions("product:categorybrandrelation:list")
	public R catelogList(@RequestParam("brandId") Long brandId)
	{
		LambdaQueryWrapper<CategoryBrandRelationEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(CategoryBrandRelationEntity::getBrandId, brandId);
		List<CategoryBrandRelationEntity> list = categoryBrandRelationService.list(wrapper);
		return R.ok().put("data", list);
	}


	/**
	 * product/categorybrandrelation/brands/list
	 */
	@GetMapping("/brands/list")
	public R getBrandByCatId(@RequestParam Long catId)
	{
		List<CategoryBrandRelationEntity> categoryBrandRelationEntity = categoryBrandRelationService.getBrandByCatId(catId);
		return R.ok().put("data", categoryBrandRelationEntity);
	}


	/**
	 * 列表
	 */
	@RequestMapping("/list")
	//  @RequiresPermissions("product:categorybrandrelation:list")
	public R list(@RequestParam Map<String, Object> params)
	{
		PageUtils page = categoryBrandRelationService.queryPage(params);
		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	//@RequiresPermissions("product:categorybrandrelation:info")
	public R info(@PathVariable("id") Long id)
	{
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);
		return R.ok().put("categoryBrandRelation", categoryBrandRelation);
	}


	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//    @RequiresPermissions("product:categorybrandrelation:save")
	public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation)
	{
		categoryBrandRelationService.saveDetail(categoryBrandRelation);
		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//  @RequiresPermissions("product:categorybrandrelation:update")
	public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation)
	{
		categoryBrandRelationService.updateById(categoryBrandRelation);
		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("product:categorybrandrelation:delete")
	public R delete(@RequestBody Long[] ids)
	{
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));
		return R.ok();
	}

}
