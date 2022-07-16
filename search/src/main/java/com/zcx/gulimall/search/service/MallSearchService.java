package com.zcx.gulimall.search.service;

import com.zcx.gulimall.search.vo.SearchParam;
import com.zcx.gulimall.search.vo.SearchRes;


public interface MallSearchService
{

	/*
	 * 条件查询
	 * @param 检索所有参数
	 * @return  返回的检索结果
	 */
	SearchRes search(SearchParam param);
}
