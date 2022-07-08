package com.zcx.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.zcx.gulimall.ware.vo.FinishVo;
import com.zcx.gulimall.ware.vo.MergeVo;
import javafx.beans.property.adapter.JavaBeanBooleanProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zcx.gulimall.ware.entity.PurchaseEntity;
import com.zcx.gulimall.ware.service.PurchaseService;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.R;


/**
 * 采购信息
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-21 18:17:14
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController
{
	@Autowired
	private PurchaseService purchaseService;

	/**
	 * 列表
	 */
	@RequestMapping("/unreceive/list")
	//  @RequiresPermissions("ware:purchase:list")
	public R getNoJob(@RequestParam Map<String, Object> params)
	{
		PageUtils page = purchaseService.getNoJob(params);
		return R.ok().put("page", page);
	}


	/***
	 * 合并采购需求
	 *
	 */

	@PostMapping("/merge")
	public R unreceiveList(@RequestBody MergeVo mergeVo)
	{
		return purchaseService.merge(mergeVo);
	}


	/***
	 * 完成采购订单
	 *
	 */

	@PostMapping("/done")
	public R finish(@RequestBody FinishVo vo)
	{

		purchaseService.finish(vo);

		return R.ok();

	}


	/***
	 * 领取采购订单
	 *
	 */

	@PostMapping("/received")
	public R unreceiveList(@RequestBody List<Long> ids)
	{


		purchaseService.receive(ids);
		return R.ok();
	}



	/**
	 * 列表
	 */
	@RequestMapping("/list")
	//  @RequiresPermissions("ware:purchase:list")
	public R list(@RequestParam Map<String, Object> params)
	{
		PageUtils page = purchaseService.queryPage(params);
		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	//@RequiresPermissions("ware:purchase:info")
	public R info(@PathVariable("id") Long id)
	{
		PurchaseEntity purchase = purchaseService.getById(id);

		return R.ok().put("purchase", purchase);
	}
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//    @RequiresPermissions("ware:purchase:save")
	public R save(@RequestBody PurchaseEntity purchase)
	{
		purchaseService.save(purchase);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//  @RequiresPermissions("ware:purchase:update")
	public R update(@RequestBody PurchaseEntity purchase)
	{
		purchaseService.updateById(purchase);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("ware:purchase:delete")
	public R delete(@RequestBody Long[] ids)
	{
		purchaseService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
