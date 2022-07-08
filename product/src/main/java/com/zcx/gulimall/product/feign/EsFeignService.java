package com.zcx.gulimall.product.feign;

import com.zcx.common.to.es.SkuEsModel;
import com.zcx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient("gulimall-search")
public interface EsFeignService
{
	@PostMapping("/search/product")
	 R productStatusUp(@RequestBody List<SkuEsModel> esModels);

}
