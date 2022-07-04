package com.zcx.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.common.utils.PageUtils;
import com.zcx.gulimall.ware.entity.WareInfoEntity;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author zcxaa
 * @email sunlightcs@gmail.com
 * @date 2022-06-21 18:17:14
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

	PageUtils queryPageBykey(Map<String, Object> params);
}

