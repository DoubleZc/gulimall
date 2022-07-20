package com.zcx.gulimall.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.nodes.Ingest;
import co.elastic.clients.json.JsonData;
import com.zcx.common.to.es.SkuEsModel;
import com.zcx.gulimall.search.constant.EsContant;
import com.zcx.gulimall.search.service.MallSearchService;
import com.zcx.gulimall.search.vo.SearchParam;
import com.zcx.gulimall.search.vo.SearchRes;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class MallSearchServiceImpl implements MallSearchService
{

	@Autowired
	ElasticsearchClient client;



	@Override
	public SearchRes search(SearchParam param)
	{
		SearchRes res = new SearchRes();
		SearchRequest request = buildSearchRequest(param,res);

		try {

			System.out.println(request);

			SearchResponse<SkuEsModel> search = client.search(request, SkuEsModel.class);



			//查询结果数据
			List<Hit<SkuEsModel>> hits = search.hits().hits();
			List<SkuEsModel> collect = hits.stream().map(hit->{
				SkuEsModel source = hit.source();
				if (!hit.highlight().isEmpty())
				{
					List<String> list = hit.highlight().get("skuTitle");
					String s = list.get(0);
					assert source != null;
					source.setSkuTitle(s);
				}
				return  source;
					}
			).collect(Collectors.toList());
			res.setProducts(collect);

			//分页信息
			assert search.hits().total() != null;
			long total = search.hits().total().value();
			res.setTotal(total);
			int sumPage = (int) Math.ceil(total / (double) EsContant.PRODUCT_PAGESIZA);
			List<Integer>pag=new ArrayList<>();
			for (int i = 1; i <= sumPage; i++) {
				pag.add(i-1,i);
			}
			res.setPag(pag);
			res.setTotalPages(sumPage);
			Integer pageNum = param.getPageNum();
			res.setPageNum(pageNum);



			//聚合信息
			//三级分类
			LongTermsAggregate catalog_agg = search.aggregations().get("catalog_agg").lterms();
			Buckets<LongTermsBucket> buckets = catalog_agg.buckets();
			List<LongTermsBucket> array = buckets.array();
			List<SearchRes.Catalog> catalogList = array.stream().map(longTermsBucket -> {
				SearchRes.Catalog catalog = new SearchRes.Catalog();
				String catalogId = longTermsBucket.key();
				catalog.setCatalogId(Long.valueOf(catalogId));

				StringTermsAggregate catalogName_agg = longTermsBucket.aggregations().get("catalogName_agg").sterms();
				String catalogName = catalogName_agg.buckets().array().get(0).key();

				catalog.setCatalogName(catalogName);
				return catalog;
			}).collect(Collectors.toList());
			res.setCatalogs(catalogList);


			//品牌分类
			LongTermsAggregate brand_agg = search.aggregations().get("brand_agg").lterms();
			List<SearchRes.Brand> brandList = brand_agg.buckets().array().stream().map(
					longTermsBucket -> {
						SearchRes.Brand brand = new SearchRes.Brand();
						String brandId = longTermsBucket.key();
						brand.setBrandId(Long.valueOf(brandId));

						StringTermsAggregate brandImg_agg = longTermsBucket.aggregations().get("brandImg_agg").sterms();
						String brandImg = brandImg_agg.buckets().array().get(0).key();
						brand.setBrandImg(brandImg);

						StringTermsAggregate brandName_agg = longTermsBucket.aggregations().get("brandName_agg").sterms();
						String brandName = brandName_agg.buckets().array().get(0).key();
						brand.setBrandName(brandName);

						return brand;
			}).collect(Collectors.toList());
			res.setBrands(brandList);



			//属性分类
			NestedAggregate attr_agg = search.aggregations().get("attr_agg").nested();
			List<SearchRes.Attr> attrList = attr_agg.aggregations().get("attr_agg").lterms().buckets().array().stream().map(
					longTermsBucket -> {
						SearchRes.Attr attr = new SearchRes.Attr();
						String attrId = longTermsBucket.key();
						attr.setAttrId(Long.valueOf(attrId));

						StringTermsAggregate attrValue = longTermsBucket.aggregations().get("attr_agg-Value_agg").sterms();
						List<String> aValue = attrValue.buckets().array().stream().map(StringTermsBucket::key).collect(Collectors.toList());
						attr.setAttrValue(aValue);


						StringTermsAggregate attrName = longTermsBucket.aggregations().get("attr_agg-Name_agg").sterms();
						String aName = attrName.buckets().array().get(0).key();
						attr.setAttrName(aName);

						return attr;
					}).collect(Collectors.toList());
			res.setAttrs(attrList);


			Map<Long, String> attrMap = attrList.stream().collect(Collectors.toMap(SearchRes.Attr::getAttrId,SearchRes.Attr::getAttrName));


			System.out.println("=================");
			//面包屑导航数据

			if (param.getAttrs()!=null&&!param.getAttrs().isEmpty()&&!attrList.isEmpty()) {
				List<SearchRes.Nav> collect1 = param.getAttrs().stream().map(a -> {
					SearchRes.Nav nav = new SearchRes.Nav();
					String[] s = a.split("_");
					String attrNmame = attrMap.get(Long.valueOf(s[0]));

					String url = param.get_url();
					try {

						a = URLEncoder.encode(a,"utf-8");
						a = a.replace("+", "%20");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

					System.out.println(url);
					System.out.println(a);
					String replace = url.replace("attrs="+a+"&", "").replace("&"+"attrs="+a, "");
					String _url = replace.replace( "attrs="+a, "");

					System.out.println(_url);
					nav.setNavName(attrNmame);
					nav.setNavValue(s[1]);
					nav.setLink("http://search.gulimall.com/list.html?"+_url);
					return nav;
				}).collect(Collectors.toList());

				collect1.forEach(System.out::println);
				res.setNavs(collect1);
			}










		} catch (IOException e) {
			e.printStackTrace();
		}

		return res;
	}

	public SearchRequest buildSearchRequest(SearchParam param,SearchRes res)
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
						if (Strings.isNotEmpty(s[0])) {
							//1_500
							r.field("skuPrice").gte(JsonData.of(Double.valueOf(s[0])));
							if (s.length==2)
							{
								//100_
								r.field("skuPrice").lte(JsonData.of(Double.valueOf(s[1])));
							}

						}
						else
						{
							//_100
							r.field("skuPrice").lte(JsonData.of(Double.valueOf(s[1])));
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
													attrM.term(attrTerm1 -> {
																attrTerm1.field("attrs.attrId").value(s[0]);
																res.getAttrIds().add(Long.valueOf(s[0]));
																return attrTerm1;
															}
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
		if (Strings.isNotEmpty(param.getSort())) {
			SortOptions.Builder sortQuery = new SortOptions.Builder();
			String[] s = param.getSort().split("_");
			FieldSort.Builder builder = new FieldSort.Builder();
			SortOrder order = s[1].equalsIgnoreCase(SortOrder.Desc.jsonValue()) ? SortOrder.Desc : SortOrder.Asc;
			builder.field(s[0]).order(order);
			sortQuery.field(builder.build());
			requestBuilder.sort(sortQuery.build());
		}


		/*分页*/
		requestBuilder.from((param.getPageNum() - 1) * EsContant.PRODUCT_PAGESIZA);
		requestBuilder.size(EsContant.PRODUCT_PAGESIZA);


		/*高亮*/
		if (Strings.isNotEmpty(param.getKeyword())) {
			Highlight.Builder hLight = new Highlight.Builder();
			HighlightField.Builder hField = new HighlightField.Builder();
			hField.preTags("<b style='color:red'>");
			hField.postTags("</b>");
			hLight.fields("skuTitle", hField.build());
			requestBuilder.highlight(hLight.build());
		}


		/*聚合*/
		//品牌聚合
		requestBuilder.aggregations("brand_agg", brandAgg -> {
			brandAgg.aggregations("brandName_agg", nameAgg ->
					nameAgg.terms(terms -> terms.field("brandName").size(10))
			);
			brandAgg.aggregations("brandImg_agg", imgAgg ->
					imgAgg.terms(terms -> terms.field("brandImg").size(10))
			);
			return brandAgg.terms(terms -> terms.field("brandId").size(10));
		});

		//三级分类聚合
		requestBuilder.aggregations("catalog_agg", catalogAgg -> {
			catalogAgg.aggregations("catalogName_agg", nameAgg ->
					nameAgg.terms(term -> term.field("catalogName").size(10))
			);
			return catalogAgg.terms(term -> term.field("catalogId").size(10));
		});


		//属性聚合
		requestBuilder.aggregations("attr_agg", attrAgg -> {
			attrAgg.aggregations("attr_agg", attrN -> {
				attrN.aggregations("attr_agg-Name_agg", nameAgg ->
						nameAgg.terms(term -> term.field("attrs.attrName").size(10))
				);
				attrN.aggregations("attr_agg-Value_agg", valueAgg ->
						valueAgg.terms(term -> term.field("attrs.attrValue").size(10))
				);
				return attrN.terms(term -> term.field("attrs.attrId").size(10));
			});
			return attrAgg.nested(n -> n.path("attrs"));
		});


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
