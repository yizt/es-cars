package org.es.framework.elasticsearch;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSONArray;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.es.framework.EsCarsApp;
import org.es.cars.es.model.OrgPatientInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EsCarsApp.class)
@Rollback(false)
@ActiveProfiles("dev")
public class TestElasticSearchQuery {

	@Resource
	private ElasticsearchTemplate elasticsearchTemplate;


	@Test
	public void testQueryById() {

		SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices("patient_bak_index")
				.withTypes("org_patient_info").withQuery(QueryBuilders.matchAllQuery()).withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.termQuery("patient_identity_no", "430102192412230513")))
				.build();
		Page<OrgPatientInfo> pg = elasticsearchTemplate.queryForPage(searchQuery, OrgPatientInfo.class);
		System.out.println(pg.getContent());
	}

	@Test
	public void testQueryAgg() {

		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery())
				.withIndices("patient_bak_index").withTypes("org_patient_info").addAggregation(AggregationBuilders.terms("sn").field("sex_name"))
				.build();
		Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
			@Override
			public Aggregations extract(SearchResponse response) {
				return response.getAggregations();
			}
		});
		List<Terms.Bucket> buckets = ((StringTerms)aggregations.getAsMap().get("sn")).getBuckets();
		for (Terms.Bucket bucket : buckets) {
			System.out.println(bucket.getKeyAsString()+":"+bucket.getDocCount());
		}
	}


	@Test
	public void testDischarges() {

		//聚合查询
		TermsAggregationBuilder tb = AggregationBuilders.terms("sex_name").field("sex_name").order(Terms.Order.count(true));

		//查询条件
		BoolQueryBuilder bqb = QueryBuilders.boolQuery();
		//医院（org_id）
		bqb.must(QueryBuilders.termQuery("org_id",""));
		//出院时间（dishospital_date）
		bqb.must(QueryBuilders.rangeQuery("dishospital_date").gt(""));
		bqb.must(QueryBuilders.rangeQuery("dishospital_date").lt(""));
		//科室（dishospital_dept_id）
		bqb.must(QueryBuilders.termQuery("dishospital_dept_id",""));
		//性别（sex_name）
		bqb.must(QueryBuilders.termQuery("sex_name",""));
		//住院天数(inp_days)
		bqb.must(QueryBuilders.rangeQuery("inp_days").gt(""));
		bqb.must(QueryBuilders.rangeQuery("inp_days").lt(""));
		//年龄（age_year）
		bqb.must(QueryBuilders.rangeQuery("age_year").gt(""));
		bqb.must(QueryBuilders.rangeQuery("age_year").lt(""));

		//查询条件
		BoolQueryBuilder bqb2 = QueryBuilders.boolQuery();
		bqb2.must(QueryBuilders.termQuery("sex_name","男"));
		//bqb2.must(QueryBuilders.rangeQuery("age_year").gt("60"));
		//bqb2.must(QueryBuilders.rangeQuery("age_year").lt("70"));

		bqb2.must(QueryBuilders.rangeQuery("dishospital_date").gt("2015-01-01 00:00:00"));
		bqb2.must(QueryBuilders.rangeQuery("dishospital_date").lt("2017-01-01 00:00:00"));
		//执行查询
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(bqb2)
				.withIndices("patient_bak_index").withTypes("inp_rec_inp_mr_page").addAggregation(tb)
				.build();
		Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
			@Override
			public Aggregations extract(SearchResponse response) {
				return response.getAggregations();
			}
		});
		List<Terms.Bucket> buckets = ((StringTerms)aggregations.getAsMap().get("sex_name")).getBuckets();
		Map<String,Object> _result = new HashMap<String,Object>();
		for (Terms.Bucket bucket : buckets) {
			_result.put(bucket.getKeyAsString(),bucket.getDocCount());
			//System.out.println(bucket.getKeyAsString()+":"+bucket.getDocCount());
		}
		System.out.println(JSONArray.toJSONString(_result));//{"女":1045,"男":928} //{"男":928}//{"男":555}//{"男":91}//{"男":477}
		//return JSON.toJSON(ResultSet.ok(JSONArray.toJSONString(_result))).toString();
	}

}
