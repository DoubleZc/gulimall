package com.zcx.gulimall.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.json.JsonData;
import com.zcx.gulimall.search.constant.EsContant;
import com.zcx.gulimall.search.service.MallSearchService;
import com.zcx.gulimall.search.vo.SearchParam;
import com.zcx.gulimall.search.vo.SearchRes;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class MallSearchServiceImpl implements MallSearchService
{

	@Autowired
	ElasticsearchClient client;
	private HighlightField.Builder hField;


	@Override
	public SearchRes search(SearchParam param)
	{

		SearchRequest request = buildSearchRequest(param);
		try {
			SearchResponse<SearchRes> search = client.search(request, SearchRes.class);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public SearchRequest buildSearchRequest(SearchParam param)
	{
		/*SearchRequest request1 = new SearchRequest.Builder().index(EsContant.PRODUCT_INDEX).query(q -> {
			return q.bool(b -> {
				//若有关键字，查询must条件
				if (Strings.isNotEmpty(param.getKeyword())) {
					b.must(m ->
							//根据Keyword查询skuTitle
							m.match(match ->
									match.field("skuTitle").query(param.getKeyword())
							)
					);
				}
				//过滤
				if (param.getCatalog3Id() != null)
					b.filter(f ->
							//若有三级分类，过滤三级分类
							f.term(t ->
									t.field("catalogId").value(param.getCatalog3Id())
							)
					);
				if (param.getBrandId() != null && !param.getBrandId().isEmpty())
				b.filter(f ->
					//若有品牌分类，过滤品牌分类
						f.terms(t ->
								t.field("brandId").terms(term -> term.value(getLongList(param.getBrandId())))
						)
				);
				if (param.getAttrs() != null && !param.getAttrs().isEmpty())
				b.filter(f -> {
					//按照属性查   attr=1_海思:塞班
						List<String> attrs = param.getAttrs();
						//for循环
						attrs.forEach(attr -> {
							String[] s = attr.split("_");
							f.nested(n ->
									n.path("attrs").query(
											attrQ -> attrQ.bool(attrB ->
													attrB.must(attrM -> {
														//第一个条件id相等
														attrM.term(attrTerm1 ->
																attrTerm1.field("attrs.attrId").value(s[0])
														);
														String[] attrValue = s[1].split(":");
														List<String> list = Arrays.asList(attrValue);
														List<FieldValue> stringList = getStringList(list);
														//第二个条件value相等
														attrM.terms(attrTerms ->
																attrTerms.field("attrs.attrValue").terms(term2 ->
																		term2.value(stringList)
																)
														);
														return attrM;
													})
											)
									)
							);
						});
					return f;
				});
				b.filter(f -> {
					//按照库存查
					if (param.getHasStock() != null) {
						f.term(t ->
								t.field("hasStock").value(param.getHasStock() == 1)
						);
					}
					return f;
				});
				b.filter(f -> {
					//按照价格区间  price=1_500   _500  500_
					if (Strings.isNotEmpty(param.getSkuPrice())) {
						f.range(r -> {
							String[] s = param.getSkuPrice().split("_");
							if (s.length == 2)
								//1_500
								r.field("skuPrice").gte(JsonData.of(s[0])).lte(JsonData.of(s[1]));
							if (s.length == 1) {
								if (param.getSkuPrice().startsWith("_")) {
									//_500
									r.field("skuPrice").lte(JsonData.of(s[0]));
								} else {
									//500_
									r.field("skuPrice").gte(JsonData.of(s[0]));
								}
							}
							return r;
						});
					}
					return f;
				});
				return b;
			});
		}).build();
*/


		SearchRequest.Builder requestBuilder = new SearchRequest.Builder();
		requestBuilder.index(EsContant.PRODUCT_INDEX);
		/*查询条件*/
		BoolQuery.Builder boolQuery = new BoolQuery.Builder();
		//若有关键字，查询must条件
		if (Strings.isNotEmpty(param.getKeyword())) {
			boolQuery.must(MatchQuery.of(match ->
					match.field("skuTitle").query(param.getKeyword()))._toQuery());
		}
		//若有三级分类，过滤三级分类
		if (param.getCatalog3Id() != null) {
			boolQuery.filter(QueryBuilders.term(t ->
					t.field("catalogId").value(param.getCatalog3Id())));
		}
		//若有品牌分类，过滤品牌分类
		if (param.getBrandId() != null && !param.getBrandId().isEmpty()) {
			boolQuery.filter(QueryBuilders.terms(t ->
					t.field("brandId").terms(terms -> terms.value(getLongList(param.getBrandId())))
			));
		}
		//按照库存查
		if (param.getHasStock() != null) {
			boolQuery.filter(TermQuery.of(t ->
					t.field("hasStock").value(param.getHasStock() == 1))._toQuery()
			);
		}
		//按照价格区间  price=1_500   _500  500_
		if (Strings.isNotEmpty(param.getSkuPrice())) {
			boolQuery.filter(RangeQuery.of(
					r -> {
						String[] s = param.getSkuPrice().split("_");
						if (s.length == 2)
							//1_500
							r.field("skuPrice").gte(JsonData.of(s[0])).lte(JsonData.of(s[1]));
						if (s.length == 1) {
							if (param.getSkuPrice().startsWith("_")) {
								//_500
								r.field("skuPrice").lte(JsonData.of(s[0]));
							} else {
								//500_
								r.field("skuPrice").gte(JsonData.of(s[0]));
							}
						}
						return r;
					}
					)._toQuery()
			);
		}
		//按照属性查   attr=1_海思:塞班
		if (param.getAttrs() != null && !param.getAttrs().isEmpty()) {
			List<String> attrs = param.getAttrs();
			//for循环
			attrs.forEach(attr -> {
				String[] s = attr.split("_");
				boolQuery.filter(NestedQuery.of(n ->
						n.path("attrs").query(
								attrQ -> attrQ.bool(attrB ->
										{
											attrB.must(attrM ->
													//第一个条件id相等
													attrM.term(attrTerm1 ->
															attrTerm1.field("attrs.attrId").value(s[0])
													));
											attrB.must(attrM -> {
												String[] attrValue = s[1].split(":");
												List<String> list = Arrays.asList(attrValue);
												List<FieldValue> stringList = getStringList(list);
												//第二个条件value相等
												attrM.terms(attrTerms ->
														attrTerms.field("attrs.attrValue").terms(term2 ->
																term2.value(stringList)
														)
												);
												return attrM;
											});
											return attrB;
										}
								)
						)
				)._toQuery());
			});
		}
		requestBuilder.query(boolQuery.build()._toQuery());


		/*排序条件*/
		//sort=hotscore_asc/desc
		if (Strings.isNotEmpty(param.getSort())){
			SortOptions.Builder sortQuery = new SortOptions.Builder();
			String[] s = param.getSort().split("_");
			FieldSort.Builder builder = new FieldSort.Builder();
			SortOrder order = s[1].equalsIgnoreCase(SortOrder.Desc.jsonValue())?SortOrder.Desc:SortOrder.Asc;
			builder.field(s[0]).order(order);
			sortQuery.field(builder.build());
			requestBuilder.sort(sortQuery.build());
		}



		/*分页*/
		requestBuilder.from((param.getPageNum() - 1) * EsContant.PRODUCT_PAGESIZA);
		requestBuilder.size(EsContant.PRODUCT_PAGESIZA);

		/*高亮*/
		if (Strings.isNotEmpty(param.getKeyword())){
			Highlight.Builder hLight = new Highlight.Builder();
			HighlightField.Builder hField = new HighlightField.Builder();
			hField.preTags("<b style='color:red'>");
			hField.postTags("</b>");
			hLight.fields("skuTitle",hField.build());
			requestBuilder.highlight(hLight.build());
		}

		return requestBuilder.build();
	}


	private List<FieldValue> getLongList(List<Long> list)
	{
		return list.stream().map(
				FieldValue::of
		).collect(Collectors.toList());
	}

	private List<FieldValue> getStringList(List<String> list)
	{
		return list.stream().map(
				FieldValue::of
		).collect(Collectors.toList());
	}

}
