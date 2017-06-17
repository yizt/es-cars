package org.es.cars.web.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.es.framework.util.json.ResultSet;
import org.es.framework.utils.ESQueryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
 * Created by zhangw on 2017/4/21.
 * 医技指标
 */

@Controller
@RequestMapping(value = "/rest/skill")
public class SkillController {
    Logger logger  =  LoggerFactory.getLogger(SkillController.class );

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;
    @Value("${index.name}")
    private String index;
    @SuppressWarnings("unchecked")
    @RequestMapping("labInfo")
    @ResponseBody
    @POST
    /**
     * 获取检验数
     * 维度包括：
     * 时间：lab_date（检验日期）、
     * 医院：org_id、
     * 科室：dept_name、
     * 性别：patient_sex_name、
     * 年龄：patient_age_year
     *
     */
    public String getLabCount(@RequestBody String body) {
        logger.info("获取检验数/rest/skill/labInfo 参数:"+body);

        Map<String,List<Map<Object,Object>>> map = JSONArray.parseObject(body, Map.class);
        List<Map<Object, Object>> filter = map.get("filter");
        List<Map<Object,Object>> group = map.get("group");

        //过滤条件
        AbstractQueryBuilder queryBuilder = ESQueryUtils.getQueryBuilder(filter);

        //分组条件
        Stream<AbstractAggregationBuilder> aggStream = ESQueryUtils.getAggregationBuilders(group);

        AbstractAggregationBuilder agg = ESQueryUtils.tansformGroup(aggStream);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().
                withQuery(queryBuilder).
                withIndices(index).
                withTypes("inp_lab_info","outp_lab_info").
                addAggregation(agg).
        build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });
        //解析结果
        Stream<LinkedHashMap<String, String>> stream = ESQueryUtils.parse(aggregations);
        String result = JSON.toJSONString(ResultSet.ok(stream.toArray()));
        logger.info("获取检验数/rest/skill/labInfo 结果:"+result);

        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("examInfo")
    @ResponseBody
    @POST
    /**
     * 获取检查数
     * 维度包括：
     * 时间：exam_date（检查日期）、
     * 医院：org_id、
     * 科室：dept_name、
     * 性别：patient_sex_name、
     * 年龄：patient_age_year
     *
     */
    public String getExamCount(@RequestBody String body) {
        logger.info("获取检查数/rest/skill/examInfo 参数:"+body);

        Map<String,List<Map<Object,Object>>> map = JSONArray.parseObject(body, Map.class);
        List<Map<Object, Object>> filter = map.get("filter");
        List<Map<Object,Object>> group = map.get("group");

        //过滤条件
        AbstractQueryBuilder queryBuilder = ESQueryUtils.getQueryBuilder(filter);

        //分组条件
        Stream<AbstractAggregationBuilder> aggStream = ESQueryUtils.getAggregationBuilders(group);

        AbstractAggregationBuilder agg = ESQueryUtils.tansformGroup(aggStream);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().
                withQuery(queryBuilder).
                withIndices(index).
                withTypes("inp_exam_info","outp_exam_info").
                addAggregation(agg).
                build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });
        //解析结果
        Stream<LinkedHashMap<String, String>> stream = ESQueryUtils.parse(aggregations);
        String result = JSON.toJSONString(ResultSet.ok(stream.toArray()));
        logger.info("获取检查数/rest/skill/examInfo 结果:"+result);

        return result;
    }

}
