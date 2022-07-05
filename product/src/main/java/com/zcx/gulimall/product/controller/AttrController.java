package com.zcx.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.zcx.gulimall.product.entity.ProductAttrValueEntity;
import com.zcx.gulimall.product.service.ProductAttrValueService;
import com.zcx.gulimall.product.vo.AttrRespVo;
import com.zcx.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zcx.gulimall.product.entity.AttrEntity;
import com.zcx.gulimall.product.service.AttrService;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.R;


/**
 * 商品属性
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-20 18:41:22
 */
@RestController
@RequestMapping("product/attr")
public class AttrController
{
	@Autowired
	private AttrService attrService;

	@Autowired
	ProductAttrValueService productAttrValueService;

	//    http://localhost:88/api/product/attr/base/list/0?t=1656656300437&page=1&limit=10&key=
	@GetMapping("/{type}/list/{catelogId}")
	public R list(@RequestParam Map<String, Object> params, @PathVariable Long catelogId, @PathVariable String type)
	{
		PageUtils pageUtils = attrService.queryBasePage(params, catelogId, type);
		return R.ok().put("page", pageUtils);
	}


	/***
	 * 修改商品规格
	 * /product/attr/base/listforspu/{spuId}
	 */
	@RequestMapping("/base/listforspu/{spuId}")
	public R baseAttrListForSpu(@PathVariable Long spuId)
	{
		List<ProductAttrValueEntity> entities = productAttrValueService.baseAttrListForSpu(spuId);

		return R.ok().put("data", entities);

	}


	/***
	 * 修改商品规格
	 * /product/attr/update/{spuId}
	 */
	@RequestMapping("/update/{spuId}")
	public R updateSpu(@PathVariable Long spuId, @RequestBody List<ProductAttrValueEntity> productAttrValueEntity
	)
	{

		productAttrValueService.updateSpu(spuId, productAttrValueEntity);

		return R.ok();

	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{attrId}")
	//@RequiresPermissions("product:attr:info")
	public R info(@PathVariable("attrId") Long attrId)
	{
		AttrRespVo attr = attrService.attrInfo(attrId);
		return R.ok().put("attr", attr);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//    @RequiresPermissions("product:attr:save")
	public R save(@RequestBody AttrVo attr)
	{
		attrService.saveAttr(attr);
		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//  @RequiresPermissions("product:attr:update")
	public R update(@RequestBody AttrVo attr)
	{
		attrService.updateAttr(attr);
		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("product:attr:delete")
	public R delete(@RequestBody Long[] attrIds)
	{
		attrService.removeByIds(Arrays.asList(attrIds));

		return R.ok();
	}

}
