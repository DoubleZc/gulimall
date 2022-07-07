package com.zcx.gulimall.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest(classes = SearchApplication.class)
class SearchApplicationTests
{

	@Autowired
	ElasticsearchClient client;


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



}
