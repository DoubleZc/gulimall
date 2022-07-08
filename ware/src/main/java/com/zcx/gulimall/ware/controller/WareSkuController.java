package com.zcx.gulimall.ware.controller;

import java.util.*;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.zcx.common.constant.WareConstant;
import com.zcx.gulimall.ware.vo.FinishDetailVo;
import com.zcx.gulimall.ware.vo.FinishVo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zcx.gulimall.ware.entity.WareSkuEntity;
import com.zcx.gulimall.ware.service.WareSkuService;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.R;


/**
 * 商品库存
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-21 18:17:14
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController
{
	@Autowired
	private WareSkuService wareSkuService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	//  @RequiresPermissions("ware:waresku:list")
	public R list(@RequestParam Map<String, Object> params)
	{
		PageUtils page = wareSkuService.queryPageByCondition(params);

		return R.ok().put("page", page);
	}





	/**
	 * 通过skuid查看库存情况
	 */
	@GetMapping("/{skuId}")
	public R getBySkuId(@PathVariable Long skuId)
	{
		Integer entity = wareSkuService.getBySkuId(skuId);
		return R.ok().put("data",entity);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	//@RequiresPermissions("ware:waresku:info")
	public R info(@PathVariable("id") Long id)
	{
		WareSkuEntity wareSku = wareSkuService.getById(id);

		return R.ok().put("wareSku", wareSku);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//    @RequiresPermissions("ware:waresku:save")
	public R save(@RequestBody WareSkuEntity wareSku)
	{
		wareSkuService.save(wareSku);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//  @RequiresPermissions("ware:waresku:update")
	public R update(@RequestBody WareSkuEntity wareSku)
	{
		wareSkuService.updateById(wareSku);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("ware:waresku:delete")
	public R delete(@RequestBody Long[] ids)
	{
		wareSkuService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
