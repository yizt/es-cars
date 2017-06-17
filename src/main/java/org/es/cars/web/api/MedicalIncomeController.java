package org.es.cars.web.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.es.framework.util.json.ResultSet;
import org.es.framework.utils.ESQueryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.ws.rs.POST;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by liuyoucai on 2017/4/24.
 * 门急诊总费用
 */
@Controller
@Configuration
@RequestMapping(value = "/rest/medicalIn")
public class MedicalIncomeController {
    Logger logger  =  LoggerFactory.getLogger(MedicalIncomeController.class );
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @Value("${index.name}")
    private String index;

    @SuppressWarnings("unchecked")
    @RequestMapping("outpIncome")
    @ResponseBody
    @POST
    /**
     * 获取门急诊总费用
     * 维度：时间、医院、科室、费用项
     */
    public String getOutpIncome(@RequestBody String body) {
        logger.info("门急诊总费用指标接口/rest/medicalIn/outpIncome 参数:"+body);
        Map<String,List<Map<Object,Object>>> map = JSONArray.parseObject(body, Map.class);
        List<Map<Object, Object>> filter = map.get("filter");
        List<Map<Object,Object>> group = map.get("group");

        //过滤条件
        AbstractQueryBuilder queryBuilder = ESQueryUtils.getQueryBuilder(filter);

        //分组条件
        Stream<AbstractAggregationBuilder> aggStream = ESQueryUtils.getAggregationBuilders(group);
         //增加门急诊总收入聚合
        aggStream = Stream.concat(aggStream, Stream.of(AggregationBuilders.sum("cost_total_amount").field("cost_total_amount")));
        //聚合查询
        AbstractAggregationBuilder agg = ESQueryUtils.tansformGroup(aggStream);

        //执行查询
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withIndices(index)
                .withTypes("outp_charge_info").addAggregation(agg)
                .build();

        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });
        //获取解析后的结果
        Stream<LinkedHashMap<String, String>> stream = ESQueryUtils.parse(aggregations);
        String result = JSON.toJSONString(ResultSet.ok(stream.toArray()));
        logger.info("门急诊总费用指标接口/rest/medicalIn/outpIncome 结果:"+result);
        return result;
    }
    @SuppressWarnings("unchecked")
    @RequestMapping("dischargeIncome")
    @ResponseBody
    @POST
    /**
     * 出院总收入
     * 维度：时间、医院、科室、费用项
     */
    public String getDischargeIncome(@RequestBody String body) {
        logger.info("出院总收入指标接口/rest/medicalIn/dischargeIncome 参数:"+body);
        Map<String,List<Map<Object,Object>>> map = JSONArray.parseObject(body, Map.class);
        List<Map<Object, Object>> filter = map.get("filter");
        List<Map<Object,Object>> group = map.get("group");

        //过滤条件
        AbstractQueryBuilder queryBuilder = ESQueryUtils.getQueryBuilder(filter);

        //分组条件
        Stream<AbstractAggregationBuilder> aggStream = ESQueryUtils.getAggregationBuilders(group);
        //增加门急诊总收入聚合
        aggStream = Stream.concat(aggStream, Stream.of(AggregationBuilders.sum("cost_money").field("cost_money")));
        //聚合查询
        AbstractAggregationBuilder agg = ESQueryUtils.tansformGroup(aggStream);

        //执行查询
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withIndices(index)
                .withTypes("inp_bill_detail").addAggregation(agg)
                .build();

        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });
        //获取解析后的结果
        Stream<LinkedHashMap<String, String>> stream = ESQueryUtils.parse(aggregations);
        String result = JSON.toJSONString(ResultSet.ok(stream.toArray()));
        logger.info("出院总收入指标接口/rest/medicalIn/dischargeIncome 结果:"+result);
        return result;
    }
}
