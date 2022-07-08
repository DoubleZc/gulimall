package com.zcx.gulimall.search.service;


import com.zcx.common.to.es.SkuEsModel;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ProductService
{


	void productStatusUp(List<SkuEsModel> esModels) throws Exception;
}
