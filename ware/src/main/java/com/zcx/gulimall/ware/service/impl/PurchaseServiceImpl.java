package com.zcx.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.common.constant.WareConstant;
import com.zcx.common.utils.R;
import com.zcx.gulimall.ware.entity.PurchaseDetailEntity;
import com.zcx.gulimall.ware.service.PurchaseDetailService;
import com.zcx.gulimall.ware.vo.FinishDetailVo;
import com.zcx.gulimall.ware.vo.FinishVo;
import com.zcx.gulimall.ware.vo.MergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.Query;

import com.zcx.gulimall.ware.dao.PurchaseDao;
import com.zcx.gulimall.ware.entity.PurchaseEntity;
import com.zcx.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService
{

	@Autowired
	PurchaseDetailService purchaseDetailService;


	@Override
	public PageUtils queryPage(Map<String, Object> params)
	{
		IPage<PurchaseEntity> page = this.page(
				new Query<PurchaseEntity>().getPage(params),
				new QueryWrapper<PurchaseEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public PageUtils getNoJob(Map<String, Object> params)
	{
		LambdaQueryWrapper<PurchaseEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(PurchaseEntity::getStatus, 0).or().eq(PurchaseEntity::getStatus, 1);
		IPage<PurchaseEntity> page = this.page(
				new Query<PurchaseEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}

	@Transactional
	@Override
	public R merge(MergeVo mergeVo)
	{
		List<Long> items = mergeVo.getItems();
		Long purchaseId = mergeVo.getPurchaseId();

		for (Long item : items) {
			PurchaseDetailEntity byId = purchaseDetailService.getById(item);
			if (byId.getStatus() > 1) {
				return R.error("采购单已有主");
			}
		}

		if (purchaseId == null) {
			PurchaseEntity purchaseEntity = new PurchaseEntity();
			purchaseEntity.setStatus(WareConstant.PurchaseStatus.CREATED.getCode());
			save(purchaseEntity);
			purchaseId = purchaseEntity.getId();
		}


		Long finalPurchaseId = purchaseId;
		List<PurchaseDetailEntity> collect = items.stream().map(i -> {
			PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
			purchaseDetailEntity.setStatus(WareConstant.PurchaseDetail.ASSIGNED.getCode());
			purchaseDetailEntity.setId(i);
			purchaseDetailEntity.setPurchaseId(finalPurchaseId);
			return purchaseDetailEntity;

		}).collect(Collectors.toList());
		purchaseDetailService.updateBatchById(collect);

		return R.ok();

	}

	@Override
	public void receive(List<Long> ids)
	{
		List<PurchaseEntity> collect = ids.stream().map(this::getById).filter(i -> {
			return i.getStatus().equals(WareConstant.PurchaseStatus.CREATED.getCode()) ||
					i.getStatus().equals(WareConstant.PurchaseStatus.ASSIGNED.getCode());
		}).map(i -> {
			i.setStatus(WareConstant.PurchaseStatus.RECEIVE.getCode());
			return i;
		}).collect(Collectors.toList());
		if (!collect.isEmpty()) {
			updateBatchById(collect);

			collect.forEach(i -> {
				List<PurchaseDetailEntity> entities = purchaseDetailService.listDetailByPurch(i.getId());
				List<PurchaseDetailEntity> collect1 = entities.stream().map(entitie -> {
					PurchaseDetailEntity entity = new PurchaseDetailEntity();
					entity.setId(entitie.getId());
					entity.setStatus(WareConstant.PurchaseDetail.BUYING.getCode());
					return entity;
				}).collect(Collectors.toList());
				purchaseDetailService.updateBatchById(collect1);
			});

		}
	}

	@Override
	public R finish(FinishVo vo)
	{

		List<FinishDetailVo> items = vo.getItems();
		Long id = vo.getId();
		boolean flag = true;
		List<PurchaseDetailEntity> entities = new ArrayList<>();
		for (FinishDetailVo item : items) {
			PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
			if (item.getStatus().equals(WareConstant.PurchaseDetail.ERROR.getCode())) {
				purchaseDetailEntity.setStatus(WareConstant.PurchaseDetail.ERROR.getCode());
				flag = false;
			} else {
				purchaseDetailEntity.setStatus(WareConstant.PurchaseDetail.FINISH.getCode());
				purchaseDetailEntity.setId(item.getItemId());
				entities.add(purchaseDetailEntity);

				//添加入库信息ware_sku


			}
			purchaseDetailService.updateBatchById(entities);

			PurchaseEntity purchaseEntity = new PurchaseEntity();
			purchaseEntity.setId(id);
			if (flag) {
				purchaseEntity.setStatus(WareConstant.PurchaseStatus.FINISH.getCode());
				updateById(purchaseEntity);
			} else {
				purchaseEntity.setStatus(WareConstant.PurchaseStatus.ERROR.getCode());
				updateById(purchaseEntity);
			}
			return R.ok();


		}

	}