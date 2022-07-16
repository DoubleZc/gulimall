package com.zcx.gulimall.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.zcx.common.to.es.SkuEsModel;
import com.zcx.gulimall.search.service.MallSearchService;
import com.zcx.gulimall.search.service.impl.MallSearchServiceImpl;
import com.zcx.gulimall.search.vo.SearchParam;
import com.zcx.gulimall.search.vo.SearchRes;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest(classes = SearchApplication.class)
class SearchApplicationTests
{

	@Autowired
	ElasticsearchClient client;

	@Autowired
	MallSearchServiceImpl mallSearchService;


	@Test
	void contextLoads() throws Exception
	{
		Query query=  new MatchAllQuery.Builder().build()._toQuery();
		SearchRequest request=new SearchRequest.Builder().index("student").query(query).
				aggregations("ageagg",s->s.terms(t->t.field("age").size(10))).build();


		SearchResponse<Student> response = client.search(request,
				Student.class
		);

		System.out.println(request);
		List<Hit<Student>> hits = response.hits().hits();
		List<LongTermsBucket> buckets = response.aggregations()
				.get("ageagg").lterms()
				.buckets().array();
		for (LongTermsBucket bucket: buckets) {
			log.info("There are " + bucket.docCount() +
					" bikes under " + bucket.key());
		}


	}


	@Test
	void test() throws Exception
	{
		SearchParam searchParam = new SearchParam();

		searchParam.setKeyword("HUAWEI");
		searchParam.setCatalog3Id(225L);
		List<Long> ids=new ArrayList<>();
		ids.add(6L);
		ids.add(3L);
		searchParam.setBrandId(ids);
//		attr=1_海思:塞班
		List<String> attrs =new ArrayList<>();
		attrs.add("15_海思（Hisilicon）:dawd");
		searchParam.setAttrs(attrs);
		searchParam.setHasStock(0);
		searchParam.setSkuPrice("2000_");
		searchParam.setSort("skuPrice_desc");




		SearchRequest request = mallSearchService.buildSearchRequest(searchParam);
		System.out.println(request);
		SearchResponse<SkuEsModel> response = client.search(request,
				SkuEsModel.class
		);

		List<Hit<SkuEsModel>> hits = response.hits().hits();


		hits.forEach(System.out::println);


	}


	@Test
	public  void test1(){




	}



}
