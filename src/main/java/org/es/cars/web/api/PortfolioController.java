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
import org.es.cars.es.model.ESQuery;
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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * Created by liuyoucai on 2017/4/24.
 * 出院人次指标接口
 */

@Controller
@Configuration
@RequestMapping(value = "/rest/portfolio")
public class PortfolioController {
    Logger logger  =  LoggerFactory.getLogger(PortfolioController.class );

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @Value("${index.name}")
    private String index;


    @SuppressWarnings("unchecked")
    @RequestMapping("discharges")
    @ResponseBody
    @POST
    /**
     * 出院人次指标接口
     * 维度：时间、医院、科室、性别、年龄、住院天数
     */
    public String getDischarges(@RequestBody String body) {
        logger.info("出院人次指标接口/rest/portfolio/discharges 参数:"+body);
        Map<String,List<Map<Object,Object>>> map = JSONArray.parseObject(body, Map.class);
        List<Map<Object, Object>> filter = map.get("filter");
        List<Map<Object,Object>> group = map.get("group");
        String dimentions="dishospital_date,inp_dept_code,inp_dept_name,area_name,sex_name,age_year";
        String childDims="org_id,diag_code,diag_name,features_type_code";
        ESQuery esQuery=new ESQuery(Arrays.asList(dimentions.split(",")),Arrays.asList(childDims.split(",")),"inp_mr_diag");

//        //增加过滤条件 features_type_code='03' 出院诊断
//        String dis="{\"field\":\"features_type_code\",\"filter_type\":\"equal\",\"match_value\":\"03\"}";
//        filter.add(JSON.parseObject(dis,Map.class));

        //过滤条件
        AbstractQueryBuilder queryBuilder = ESQueryUtils.getQueryBuilder(filter,esQuery);

        //分组字段顺序列表
        List<String> groupOrder=ESQueryUtils.getESQueryGroups(group).map(x->x.getField()).collect(Collectors.toList());

        //分组条件
        Stream<AbstractAggregationBuilder> aggStream = ESQueryUtils.getAggregationBuilders(group,filter,esQuery);
        //添加出院总人次
        AbstractAggregationBuilder aggBuilder= AggregationBuilders.cardinality("health_event_id").field("health_event_id");
        aggStream=Stream.concat(aggStream,Stream.of(aggBuilder));

        //聚合查询
        AbstractAggregationBuilder agg = ESQueryUtils.tansformGroup(aggStream);


        //执行查询
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withIndices(index)
                .withTypes("inp_rec_inp_mr_page").addAggregation(agg)
                .build();

        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });
        //获取解析后的结果
        List<LinkedHashMap<String, String>> list = ESQueryUtils.parse(aggregations,groupOrder);
        String result = JSON.toJSONString(ResultSet.ok(list));
        logger.info("出院人次指标接口/rest/portfolio/discharges 结果:"+result);
        return result;
    }
    @SuppressWarnings("unchecked")
    @RequestMapping("outMr")
    @ResponseBody
    @POST
    /**
     * 获取门诊人次
     * 维度：时间、医院、科室、性别、年龄
     */
    public String getOutMr(@RequestBody String body) {
        logger.info("门诊人次指标接口/rest/portfolio/outMr 参数:"+body);
        Map<String,List<Map<Object,Object>>> map = JSONArray.parseObject(body, Map.class);
        List<Map<Object, Object>> filter = map.get("filter");
        List<Map<Object,Object>> group = map.get("group");

        //过滤条件
        AbstractQueryBuilder queryBuilder = ESQueryUtils.getQueryBuilder(filter);


        //分组条件
        Stream<AbstractAggregationBuilder> aggStream = ESQueryUtils.getAggregationBuilders(group);

        //添加门诊总量
        AbstractAggregationBuilder aggBuilder= AggregationBuilders.cardinality("health_event_id").field("health_event_id");
        aggStream=Stream.concat(aggStream,Stream.of(aggBuilder));

        //聚合查询
        AbstractAggregationBuilder agg = ESQueryUtils.tansformGroup(aggStream);
        //执行查询
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withIndices(index)
                .withTypes("outp_mr").addAggregation(agg)
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
        logger.info("门诊人次指标接口/rest/portfolio/outMr 结果:"+result);
        return result;
    }
    @SuppressWarnings("unchecked")
    @RequestMapping("inptime")
    @ResponseBody
    @POST
    /**
     * 住院人次指标接口
     * 维度：时间、医院、科室、性别、年龄、住院天数
     */
    public String getInpTime(@RequestBody String body) {
        logger.info("住院人次指标接口/rest/portfolio/inptime 参数:"+body);
        Map<String,List<Map<Object,Object>>> map = JSONArray.parseObject(body, Map.class);
        List<Map<Object, Object>> filter = map.get("filter");
        List<Map<Object,Object>> group = map.get("group");

        //过滤条件
        AbstractQueryBuilder queryBuilder = ESQueryUtils.getQueryBuilder(filter);

        //分组条件
        Stream<AbstractAggregationBuilder> aggStream = ESQueryUtils.getAggregationBuilders(group);

        //聚合查询
        AbstractAggregationBuilder agg = ESQueryUtils.tansformGroup(aggStream);
        //执行查询
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withIndices(index)
                .withTypes("inp_rec_inp_mr_page").addAggregation(agg)
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
        logger.info("住院人次指标接口/rest/portfolio/inptime 结果:"+result);
        return result;
    }
}
