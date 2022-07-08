package com.zcx.gulimall.search.controller;


import com.zcx.common.to.es.SkuEsModel;
import com.zcx.common.utils.ExceptionCode;
import com.zcx.common.utils.R;
import com.zcx.gulimall.search.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RequestMapping("/search")
@RestController
public class ElasticSaveController
{


	@Autowired
	ProductService productService;

	@PostMapping("/product")
	public R   productStatusUp(@RequestBody List<SkuEsModel> esModels)
	{
		try {
			productService.productStatusUp(esModels);
		} catch (Exception e) {
			log.error(e.getMessage());
			return R.error(ExceptionCode.PRODUCT_UP_ERROR);
		}
		return R.ok();


	}





}
