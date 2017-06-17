package org.es.cars.web.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.*;
import org.es.framework.util.json.ResultSet;
import org.es.framework.utils.ESQueryUtils;
import org.es.cars.es.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by mick.yi on 2017/4/21.
 * 医疗质量类指标
 */
@Controller
@RequestMapping(value = "/rest/quality")
public class QualityController {
    Logger logger  =  LoggerFactory.getLogger(QualityController.class );

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;
    @Value("${index.name}")
    private String index;

    @SuppressWarnings("unchecked")
    @RequestMapping("avginpdays")
    @ResponseBody
    @POST
    /**
     * 获取平均住院日
     * 维度：时间、医院、科室、病区、性别、年龄
     * dishospital_date,org_id,inp_dept_code,inp_dept_name,area_name,sex_name,age_year
     */
    public String getAvgInpDays(@RequestBody String body) {
        logger.info("平均住院日指标/rest/quality/avginpdays 参数：\n"+body);
        Map<String, List<Map<Object, Object>>> map = JSONArray.parseObject(body, Map.class);
        List<Map<Object, Object>> filter = map.get("filter");
        List<Map<Object, Object>> group = map.get("group");
        //过滤条件
        AbstractQueryBuilder queryBuilder = ESQueryUtils.getQueryBuilder(filter);

        //分组条件
        Stream<AbstractAggregationBuilder> aggStream = ESQueryUtils.getAggregationBuilders(group);
        //增加平均住院天数聚合
        aggStream = Stream.concat(aggStream, Stream.of(AggregationBuilders.avg("inp_days").field("inp_days")));

        AbstractAggregationBuilder agg = ESQueryUtils.tansformGroup(aggStream);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().
                withQuery(queryBuilder).
                withIndices(index).
                withTypes("inp_rec_inp_mr_page").
                addAggregation(agg).
                build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });
        //获取解析后的结果
        Stream<LinkedHashMap<String, String>> stream = ESQueryUtils.parse(aggregations);
        String result = JSON.toJSONString(ResultSet.ok(stream.toArray()));
        logger.info("平均住院日指标/rest/quality/avginpdays 结果：\n"+result);
        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("treat")
    @ResponseBody
    @POST
    /**
     * 获取治疗结果
     * 维度：时间、医院、科室、病区、性别、年龄，疾病
     * dishospital_date,org_id,inp_dept_code,inp_dept_name,area_name,sex_name,age_year，diag_code,diag_name
     */
    public String getTreatResult(@RequestBody String body) {
        logger.info("治疗结果指标/rest/quality/treat 参数：\n"+body);
        Map<String, List<Map<Object, Object>>> map = JSONArray.parseObject(body, Map.class);
        List<Map<Object, Object>> filter = map.get("filter");
        List<Map<Object, Object>> group = map.get("group");
        String dimentions="dishospital_date,inp_dept_code,inp_dept_name,area_name,sex_name,age_year";
        String childDims="org_id,diag_code,diag_name,features_type_code";
        ESQuery esQuery=new ESQuery(Arrays.asList(dimentions.split(",")),Arrays.asList(childDims.split(",")),"inp_mr_diag");
        //增加过滤条件 features_type_code='03' 出院诊断
        String infectStr="{\"field\":\"features_type_code\",\"filter_type\":\"equal\",\"match_value\":\"03\"}";
        filter.add(JSON.parseObject(infectStr,Map.class));

        //过滤条件
        AbstractQueryBuilder queryBuilder = ESQueryUtils.getQueryBuilder(filter,esQuery);

        //分组字段顺序列表
        List<String> groupOrder=ESQueryUtils.getESQueryGroups(group).map(x->x.getField()).collect(Collectors.toList());
        //分组条件
        Stream<AbstractAggregationBuilder> aggStream = ESQueryUtils.getAggregationBuilders(group,filter,esQuery);
        //增加治疗结果聚合
        aggStream = Stream.concat(aggStream, Stream.of(AggregationBuilders.terms("treat_situation").field("treat_situation")));

        AbstractAggregationBuilder agg = ESQueryUtils.tansformGroup(aggStream);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().
                withQuery(queryBuilder).
                withIndices(index).
                withTypes("inp_rec_inp_mr_page").
                addAggregation(agg).
                build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });
        //获取解析后的结果
        List<LinkedHashMap<String, String>> list = ESQueryUtils.parse(aggregations,groupOrder);
        String result = JSON.toJSONString(ResultSet.ok(list));
        logger.info("治疗结果指标/rest/quality/treat 结果：\n"+result);
        return result;
    }
    @SuppressWarnings("unchecked")
    @RequestMapping("infection")
    @ResponseBody
    @POST
    /**
     * 获取院感例数
     * 维度：时间、医院、科室、病区、性别、年龄，疾病
     * dishospital_date,org_id,inp_dept_code,inp_dept_name,area_name,sex_name,age_year，diag_code,diag_name
     */
    public String getInfection(@RequestBody String body) {
        logger.info("院感例数指标/rest/quality/infection 参数：\n"+body);
        Map<String, List<Map<Object, Object>>> map = JSONArray.parseObject(body, Map.class);
        List<Map<Object, Object>> filter = map.get("filter");
        List<Map<Object, Object>> group = map.get("group");
        String dimentions="dishospital_date,inp_dept_code,inp_dept_name,area_name,sex_name,age_year";
        String childDims="org_id,diag_code,diag_name,features_type_code";
        ESQuery esQuery=new ESQuery(Arrays.asList(dimentions.split(",")),Arrays.asList(childDims.split(",")),"inp_mr_diag");

        //增加过滤条件 features_type_code='08' 代表院感
        String infectStr="{\"field\":\"features_type_code\",\"filter_type\":\"equal\",\"match_value\":\"08\"}";
        filter.add(JSON.parseObject(infectStr,Map.class));
        //过滤条件
        AbstractQueryBuilder queryBuilder = ESQueryUtils.getQueryBuilder(filter,esQuery);

        //分组字段顺序列表
        List<String> groupOrder=ESQueryUtils.getESQueryGroups(group).map(x->x.getField()).collect(Collectors.toList());

        //分组条件
        Stream<AbstractAggregationBuilder> aggStream = ESQueryUtils.getAggregationBuilders(group,filter,esQuery);
        //增加院感例数
        AbstractAggregationBuilder aggBuilder=AggregationBuilders.cardinality("health_event_id").field("health_event_id");
        aggStream=Stream.concat(aggStream,Stream.of(aggBuilder));

        AbstractAggregationBuilder agg = ESQueryUtils.tansformGroup(aggStream);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().
                withQuery(queryBuilder).
                withIndices(index).
                withTypes("inp_rec_inp_mr_page").
                addAggregation(agg).
                build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });
        //获取解析后的结果
        List<LinkedHashMap<String, String>> list = ESQueryUtils.parse(aggregations,groupOrder);
        String result = JSON.toJSONString(ResultSet.ok(list));
        logger.info("院感例数指标/rest/quality/infection 结果：\n"+result);
        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("infectionRate")
    @ResponseBody
    @POST
    /**
     * 获取院感比例
     * 维度：时间、医院、科室、病区、性别、年龄，疾病
     * dishospital_date,org_id,inp_dept_code,inp_dept_name,area_name,sex_name,age_year，diag_code,diag_name
     */
    public String getInfectionRate(@RequestBody String body) {
        logger.info("院感比例指标/rest/quality/infectionRate 参数：\n"+body);
        Map<String, List<Map<Object, Object>>> map = JSONArray.parseObject(body, Map.class);
        List<Map<Object, Object>> filter = map.get("filter");
        List<Map<Object, Object>> group = map.get("group");
        String dimentions="dishospital_date,inp_dept_code,inp_dept_name,area_name,sex_name,age_year";
        String childDims="org_id,diag_code,diag_name";
        ESQuery esQuery=new ESQuery(Arrays.asList(dimentions.split(",")),Arrays.asList(childDims.split(",")),"inp_mr_diag");

        //过滤条件
        AbstractQueryBuilder queryBuilder = ESQueryUtils.getQueryBuilder(filter,esQuery);

        //分组字段顺序列表
        List<String> groupOrder=ESQueryUtils.getESQueryGroups(group).map(x->x.getField()).collect(Collectors.toList());
        //分组条件
        Stream<AbstractAggregationBuilder> aggStream = ESQueryUtils.getAggregationBuilders(group,filter,esQuery);
        //增加院感比例聚合
        String initScript="params._agg.infections = new HashSet();params._agg.nums = new HashSet()";
        String mapScript="if(doc.features_type_code.value == \"08\") params._agg.infections.add(doc.health_event_id.value); params._agg.nums.add(doc.health_event_id.value)";
        String combineScript="Set infects=new HashSet();Set nums=new HashSet();for (t in params._agg.infections) { infects.add(t) } for (t in params._agg.nums) { nums.add(t) } return [infects,nums]";
        String reduceScript="Set infects=new HashSet();Set nums=new HashSet(); for (a in params._aggs) if(a!=null) { infects.addAll(a[0]);nums.addAll(a[1]) }  return nums.size() == 0 ? 0d : infects.size()*1.0/nums.size()";
        AbstractAggregationBuilder scriptAgg= AggregationBuilders.scriptedMetric("infection_rate").
                initScript(new Script(initScript)).
                mapScript(new Script(mapScript)).
                combineScript(new Script(combineScript)).
                reduceScript(new Script(reduceScript));
        aggStream = Stream.concat(aggStream, Stream.of(scriptAgg));

        AbstractAggregationBuilder agg = ESQueryUtils.tansformGroup(aggStream);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().
                withQuery(queryBuilder).
                withIndices(index).
                withTypes("inp_rec_inp_mr_page").
                addAggregation(agg).
                build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });
        //获取解析后的结果
        List<LinkedHashMap<String, String>> stream = ESQueryUtils.parse(aggregations,groupOrder);
        String result = JSON.toJSONString(ResultSet.ok(stream.toArray()));
        logger.info("院感比例指标/rest/quality/infectionRate 结果：\n"+result);
        return result;
    }
    @SuppressWarnings("unchecked")
    @RequestMapping("inpRec")
    @ResponseBody
    @POST
    /**
     * 获取住院明细
     * 维度：时间、医院、疾病名称
     * dishospital_date,org_id,diag_code,diag_name
     */
    public String getInpRecInfo(@RequestBody String body) {
        logger.info("住院明细指标/rest/quality/inpRec 参数：\n"+body);
        Map<String, List<Map<Object, Object>>> map = JSONArray.parseObject(body, Map.class);
        List<Map<Object, Object>> filter = map.get("filter");
        //过滤条件
        AbstractQueryBuilder queryBuilder = ESQueryUtils.getQueryBuilder(filter);

        SearchQuery searchQuery = new NativeSearchQueryBuilder().
                withIndices(index).
                withTypes("inp_rec_inp_mr_page").
                withQuery(queryBuilder).
                build();
        Page<InpRecInfo> pg = elasticsearchTemplate.queryForPage(searchQuery, InpRecInfo.class);
        String result = JSON.toJSONString(ResultSet.ok(pg.getContent()));
        logger.info("住院明细指标/rest/quality/inpRec 结果：\n"+result);
        return result;
    }

}