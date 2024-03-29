package com.zcx.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.common.comm.CosException;
import com.zcx.common.to.mq.OrderTo;
import com.zcx.common.to.mq.StockLockTo;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.R;
import com.zcx.gulimall.ware.entity.PurchaseDetailEntity;
import com.zcx.gulimall.ware.entity.WareSkuEntity;
import com.zcx.gulimall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-21 18:17:14
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

	PageUtils queryPageByCondition(Map<String, Object> params);

	void saveWareSku(List<PurchaseDetailEntity> entity);

	Integer getBySkuId(Long skuId);
	
	Map<Long,Integer> getBySkuIds(List<Long> skuIds);
	
	R lockStock(WareSkuLockVo vo) throws CosException.NotStock;
	
	void unLock(StockLockTo to);
	
	void unLock(OrderTo to);
}

