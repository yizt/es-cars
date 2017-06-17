package org.es.cars.web.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
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
 * Created by zhangw on 2017/4/24.
 * 工作效率指标
 */

@Controller
@RequestMapping(value = "/rest/effiency")
public class EffiencyController {
    Logger logger  =  LoggerFactory.getLogger(EffiencyController.class );

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;
    @Value("${index.name}")
    private String index;
    /**
     * 全院医师日均门诊人次
     *维度：时间、医院、科室、性别、年龄
     * dept_name,org_id,sex_name,age_year,visit_datetime
     * */
    @SuppressWarnings("unchecked")
    @RequestMapping("docAvgOutp")
    @ResponseBody
    @POST
    public String getDocAvgOutpCount(@RequestBody String body) {
        logger.info("全院医师日均门诊人次/rest/effiency/docAvgOutp 参数:"+body);
        Map<String,List<Map<Object,Object>>> map = JSONArray.parseObject(body, Map.class);
        List<Map<Object, Object>> filter = map.get("filter");
        List<Map<Object,Object>> group = map.get("group");

        //过滤条件
        AbstractQueryBuilder queryBuilder = ESQueryUtils.getQueryBuilder(filter);

        //分组条件
        Stream<AbstractAggregationBuilder> aggStream = ESQueryUtils.getAggregationBuilders(group);

        String initScript="params._agg.datelist = new HashSet();params._agg.nums = [];params._agg.sd = new SimpleDateFormat(\"yyyy-MM-dd\")";
        String mapScript="params._agg.datelist.add(params._agg.sd.format(new Date(doc.visit_datetime.value)));params._agg.nums.add(1)";
        String combineScript="double nums=0; for (t in params._agg.nums) { nums+=t } return [params._agg.datelist,nums]";
        String reduceScript="Set dateSet=new HashSet();double nums=0; for (a in params._aggs) if(a!=null) {dateSet.addAll(a[0]); nums+=a[1] }  return nums/dateSet.size()/30*1.0";//3750
        AbstractAggregationBuilder scriptAgg= AggregationBuilders.scriptedMetric("docAvgOutp").
                initScript(new Script(initScript)).
                mapScript(new Script(mapScript)).
                combineScript(new Script(combineScript)).
                reduceScript(new Script(reduceScript));
        aggStream = Stream.concat(aggStream, Stream.of(scriptAgg));

        AbstractAggregationBuilder agg = ESQueryUtils.tansformGroup(aggStream);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().
                withQuery(queryBuilder).
                withIndices(index).
                withTypes("outp_mr").
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
        logger.info("全院医师日均门诊人次/rest/effiency/docAvgOutp 结果:"+result);
        return result;
    }



    /**
     * 日均门诊人次
     *维度：时间、医院、科室、性别、年龄
     * dept_name,org_id,sex_name,age_year,visit_datetime
     * */
    @SuppressWarnings("unchecked")
    @RequestMapping("avgOutp")
    @ResponseBody
    @POST
    public String getAvgOutpCount(@RequestBody String body) {
        logger.info("日均门诊人次/rest/effiency/avgOutp 参数:"+body);

        Map<String,List<Map<Object,Object>>> map = JSONArray.parseObject(body, Map.class);
        List<Map<Object, Object>> filter = map.get("filter");
        List<Map<Object,Object>> group = map.get("group");

        //过滤条件
        AbstractQueryBuilder queryBuilder = ESQueryUtils.getQueryBuilder(filter);

        //分组条件
        Stream<AbstractAggregationBuilder> aggStream = ESQueryUtils.getAggregationBuilders(group);

        String initScript="params._agg.datelist = new HashSet();params._agg.nums = [];params._agg.sd = new SimpleDateFormat(\"yyyy-MM-dd\")";
        String mapScript="params._agg.datelist.add(params._agg.sd.format(new Date(doc.visit_datetime.value)));params._agg.nums.add(1)";
        String combineScript="double nums=0; for (t in params._agg.nums) { nums+=t } return [params._agg.datelist,nums]";
        String reduceScript="Set dateSet=new HashSet();double nums=0; for (a in params._aggs) if(a!=null) {dateSet.addAll(a[0]); nums+=a[1] }  return nums/dateSet.size()*1.0";
        AbstractAggregationBuilder scriptAgg= AggregationBuilders.scriptedMetric("avgOutp").
                initScript(new Script(initScript)).
                mapScript(new Script(mapScript)).
                combineScript(new Script(combineScript)).
                reduceScript(new Script(reduceScript));
        aggStream = Stream.concat(aggStream, Stream.of(scriptAgg));

        AbstractAggregationBuilder agg = ESQueryUtils.tansformGroup(aggStream);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().
                withQuery(queryBuilder).
                withIndices(index).
                withTypes("outp_mr").
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
        logger.info("日均门诊人次/rest/effiency/avgOutp 结果:"+result);

        return result;
    }


    /**
     * 日均出诊医师数
     *维度：时间、医院、科室、性别、年龄
     * dept_name,org_id,sex_name,age_year,visit_datetime
     * */
    @SuppressWarnings("unchecked")
    @RequestMapping("docAvg")
    @ResponseBody
    @POST
    public String getDocAvgCount(@RequestBody String body) {
        logger.info("日均出诊医师数/rest/effiency/docAvg 参数:"+body);

        Map<String,List<Map<Object,Object>>> map = JSONArray.parseObject(body, Map.class);
        List<Map<Object, Object>> filter = map.get("filter");
        List<Map<Object,Object>> group = map.get("group");

        //过滤条件
        AbstractQueryBuilder queryBuilder = ESQueryUtils.getQueryBuilder(filter);

        //分组条件
        Stream<AbstractAggregationBuilder> aggStream = ESQueryUtils.getAggregationBuilders(group);

        String initScript="params._agg.datelist = new HashSet();params._agg.nums = new HashSet();params._agg.sd = new SimpleDateFormat(\"yyyy-MM-dd\")";
        String mapScript="params._agg.datelist.add(params._agg.sd.format(new Date(doc.visit_datetime.value)));params._agg.nums.add(new Date(doc.visit_datetime.value)+\"_\"+doc.doctor_code.value)";
        String combineScript="return [params._agg.datelist,params._agg.nums]";
        String reduceScript="Set dateSet=new HashSet();Set numSet=new HashSet(); for (a in params._aggs) if(a!=null) {dateSet.addAll(a[0]); numSet.addAll(a[1]) ; }  return dateSet.size()==0? 0d : numSet.size()/dateSet.size()*1.0";
        AbstractAggregationBuilder scriptAgg= AggregationBuilders.scriptedMetric("docAvg").
                initScript(new Script(initScript)).
                mapScript(new Script(mapScript)).
                combineScript(new Script(combineScript)).
                reduceScript(new Script(reduceScript));
        aggStream = Stream.concat(aggStream, Stream.of(scriptAgg));

        AbstractAggregationBuilder agg = ESQueryUtils.tansformGroup(aggStream);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().
                withQuery(queryBuilder).
                withIndices(index).
                withTypes("outp_mr").
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
        logger.info("日均出诊医师数/rest/effiency/docAvg 结果:"+result);

        return result;
    }

    /**
     * 出诊医师日均门诊人次
     *维度：时间、医院、科室、性别、年龄
     * dept_name,org_id,sex_name,age_year,visit_datetime
     * */
    @SuppressWarnings("unchecked")
    @RequestMapping("avgDocOutp")
    @ResponseBody
    @POST
    public String getAvgDocOutpCount(@RequestBody String body) {
        logger.info("出诊医师日均门诊人次/rest/effiency/avgDocOutp 参数:"+body);

        Map<String,List<Map<Object,Object>>> map = JSONArray.parseObject(body, Map.class);
        List<Map<Object, Object>> filter = map.get("filter");
        List<Map<Object,Object>> group = map.get("group");

        //过滤条件
        AbstractQueryBuilder queryBuilder = ESQueryUtils.getQueryBuilder(filter);

        //分组条件
        Stream<AbstractAggregationBuilder> aggStream = ESQueryUtils.getAggregationBuilders(group);

        String initScript="params._agg.nums = new HashSet();params._agg.outps = params._agg.nums = new HashSet();params._agg.sd = new SimpleDateFormat(\"yyyy-MM-dd\")";
        String mapScript="params._agg.nums.add(new Date(doc.visit_datetime.value)+\"_\"+doc.doctor_code.value);params._agg.nums.add(new Date(doc.visit_datetime.value)+\"_\"+1)";
        String combineScript="return [params._agg.nums,params._agg.outps]";
        String reduceScript="Set numSet=new HashSet();Set outpSet=new HashSet(); for (a in params._aggs) if(a!=null) {numSet.addAll(a[0]);outpSet.addAll(a[1]); }  return outpSet.size()==0 ? 0d : numSet.size()/outpSet.size()*1.0";
        AbstractAggregationBuilder scriptAgg= AggregationBuilders.scriptedMetric("avgDocOutp").
                initScript(new Script(initScript)).
                mapScript(new Script(mapScript)).
                combineScript(new Script(combineScript)).
                reduceScript(new Script(reduceScript));
        aggStream = Stream.concat(aggStream, Stream.of(scriptAgg));

        AbstractAggregationBuilder agg = ESQueryUtils.tansformGroup(aggStream);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().
                withQuery(queryBuilder).
                withIndices(index).
                withTypes("outp_mr").
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
        logger.info("出诊医师日均门诊人次/rest/effiency/avgDocOutp 结果:"+result);

        return result;
    }
}
