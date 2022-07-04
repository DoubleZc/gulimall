package com.zcx.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.common.utils.PageUtils;
import com.zcx.gulimall.ware.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-21 18:17:14
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

	PageUtils queryPageByCondition(Map<String, Object> params);

	List<PurchaseDetailEntity> listDetailByPurch(Long id);
}

