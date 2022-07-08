package com.zcx.gulimall.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import com.zcx.common.to.es.SkuEsModel;
import com.zcx.gulimall.search.constant.EsContant;
import com.zcx.gulimall.search.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Slf4j
@Service
public class ProductServiceImpl implements ProductService
{
	@Autowired
	ElasticsearchClient client;

	@Override
	public void productStatusUp (List<SkuEsModel> esModels) throws Exception
	{

		BulkRequest.Builder builder = new BulkRequest.Builder();
		for (SkuEsModel esModel : esModels) {
			builder.operations(op ->
					op.index(i ->
							i.index(EsContant.PRODUCT_INDEX)
									.id(String.valueOf(esModel.getSkuId()))
									.document(esModel)
					)
			);
		}
		BulkResponse result = client.bulk(builder.build());
		if (result.errors()) {
			log.error("Bulk had errors");
			for (BulkResponseItem item : result.items()) {
				if (item.error() != null) {
					log.error(item.error().reason());
				}
			}
		}

	}


}
