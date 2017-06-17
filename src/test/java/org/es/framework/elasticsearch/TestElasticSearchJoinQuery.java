package org.es.framework.elasticsearch;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.children.InternalChildren;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
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
public class TestElasticSearchJoinQuery {

	@Resource
	private ElasticsearchTemplate elasticsearchTemplate;

	@Test
	public void testQueryPatients() {
		/**
		 * 嵌套多层查询数据
		 */
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery())
				.withIndices("test_join_index").withTypes("patients")
				.withFilter(QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery())
						.must(QueryBuilders.hasChildQuery("inp_rec",
								QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery())
										.must(QueryBuilders.hasChildQuery("inp_bill_detail",
												QueryBuilders.termQuery("health_event_id",
														"000002||0000436182||16535666"),
												ScoreMode.Total)),
								ScoreMode.Total)))
				.build();
		Page<OrgPatientInfo> pg = elasticsearchTemplate.queryForPage(searchQuery, OrgPatientInfo.class);
		System.out.println(pg.getContent());
	}

	@Test
	public void testAggAndAge() throws IOException {
		/**
		 * 嵌套多层查询数据
		 */
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery())
				.withIndices("test_join_index").withTypes("patients")
				.addAggregation(AggregationBuilders.filter("sex_name", QueryBuilders.termQuery("sex_name", "男"))
						.subAggregation(AggregationBuilders.terms("sex_name").field("sex_name")
								.subAggregation(AggregationBuilders.children("inp_rec", "inp_rec")
										.subAggregation(AggregationBuilders.terms("area_name").field("area_name")))))
				.build();
		Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
			@Override
			public Aggregations extract(SearchResponse response) {
				return response.getAggregations();
			}
		});
		List<Terms.Bucket> buckets =((StringTerms) ((InternalFilter) aggregations.getAsMap().get("sex_name")).getAggregations().getAsMap().get("sex_name")).getBuckets();
		for (Terms.Bucket bucket : buckets) {
			System.out.println(bucket.getKeyAsString() + ":" + bucket.getDocCount());
			List<Terms.Bucket> areaBuckets = ((StringTerms) ((InternalChildren) bucket.getAggregations().getAsMap()
					.get("inp_rec")).getAggregations().asMap().get("area_name")).getBuckets();
			for (Terms.Bucket bucket2 : areaBuckets) {
				System.out.println(bucket2.getKeyAsString() + ":" + bucket2.getDocCount());
			}

		}
	}

}
