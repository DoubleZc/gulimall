package com.zcx.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.R;
import com.zcx.gulimall.ware.entity.PurchaseEntity;
import com.zcx.gulimall.ware.vo.FinishVo;
import com.zcx.gulimall.ware.vo.MergeVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-21 18:17:14
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);
	PageUtils getNoJob(Map<String, Object> params);

	R merge(MergeVo mergeVo);

	void receive(List<Long> ids);

	R finish(FinishVo vo);
}

