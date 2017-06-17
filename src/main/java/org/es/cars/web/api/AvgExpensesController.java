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
 * Created by liuyoucai on 2017/4/25.
 * 住院均次费用
 */
@Controller
@Configuration
@RequestMapping(value = "/rest/avgExpense")
public class AvgExpensesController {
    Logger logger  =  LoggerFactory.getLogger(AvgExpensesController.class );
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;
    @Value("${index.name}")
    private String index;

    @SuppressWarnings("unchecked")
    @RequestMapping("inpAvgExpense")
    @ResponseBody
    @POST
    /**
     * 住院均次费用
     * 维度：时间、医院、科室、费用项
     */
    public String getInpAvgExpense(@RequestBody String body) {
        logger.info("住院均次费用指标接口/rest/avgExpense/inpAvgExpense 参数:"+body);
        Map<String,List<Map<Object,Object>>> map = JSONArray.parseObject(body, Map.class);
        List<Map<Object, Object>> filter = map.get("filter");
        List<Map<Object,Object>> group = map.get("group");

        //过滤条件
        AbstractQueryBuilder queryBuilder = ESQueryUtils.getQueryBuilder(filter);

        //分组条件
        Stream<AbstractAggregationBuilder> aggStream = ESQueryUtils.getAggregationBuilders(group);
        //增加住院均次费用聚合
        String initScript="params._agg.idlist = new HashSet();params._agg.nums = []";
        String mapScript="params._agg.idlist.add(doc.health_event_id.value);params._agg.nums.add(doc.cost_money.value == \" \" ? 0 : doc.cost_money.value)";
        String combineScript="double nums=0; for (t in params._agg.nums) { nums+=t } return [params._agg.idlist,nums]";
        String reduceScript="Set totalSet=new HashSet();double nums=0; for (a in params._aggs) if(a!=null) {totalSet.addAll(a[0]); nums+=a[1] }  return totalSet.size() == 0 ? 0d : nums/totalSet.size()*1.0";
        AbstractAggregationBuilder scriptAgg= AggregationBuilders.scriptedMetric("inpAvgExpense").
                initScript(new Script(initScript)).
                mapScript(new Script(mapScript)).
                combineScript(new Script(combineScript)).
                reduceScript(new Script(reduceScript));
        aggStream = Stream.concat(aggStream, Stream.of(scriptAgg));
        //aggStream = Stream.concat(aggStream, Stream.of(AggregationBuilders.avg("cost_money").field("cost_money")));
        //聚合查询
        AbstractAggregationBuilder agg = ESQueryUtils.tansformGroup(aggStream);

        //执行查询
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withIndices(index)
                .withTypes("inp_bill_detail")
                .addAggregation(agg)
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
        logger.info("住院均次费用指标接口/rest/avgExpense/inpAvgExpense 结果:"+result);
        return result;
    }
    @SuppressWarnings("unchecked")
    @RequestMapping("outpAvgExpense")
    @ResponseBody
    @POST
    /**
     * 门急诊均次费用
     * 维度：时间、医院、科室、费用项
     */
    public String getutpAvgExpense(@RequestBody String body) {
        logger.info("门急诊均次费用指标接口/rest/avgExpense/inpAvgExpense 参数:"+body);
        Map<String,List<Map<Object,Object>>> map = JSONArray.parseObject(body, Map.class);
        List<Map<Object, Object>> filter = map.get("filter");
        List<Map<Object,Object>> group = map.get("group");

        //过滤条件
        AbstractQueryBuilder queryBuilder = ESQueryUtils.getQueryBuilder(filter);

        //分组条件
        Stream<AbstractAggregationBuilder> aggStream = ESQueryUtils.getAggregationBuilders(group);

        //门急诊均次费用
        String initScript="params._agg.idlist = new HashSet();params._agg.nums = []";
        String mapScript="params._agg.idlist.add(doc.health_event_id.value);params._agg.nums.add(doc.cost_total_amount.value == \" \" ? 0 : doc.cost_total_amount.value)";
        String combineScript="double nums=0; for (t in params._agg.nums) { nums+=t } return [params._agg.idlist,nums]";
        String reduceScript="Set totalSet=new HashSet();double nums=0; for (a in params._aggs) if(a!=null) {totalSet.addAll(a[0]); nums+=a[1] }  return totalSet.size() == 0 ? 0d : nums/totalSet.size()*1.0";
        AbstractAggregationBuilder scriptAgg= AggregationBuilders.scriptedMetric("outpAvgExpense").
                initScript(new Script(initScript)).
                mapScript(new Script(mapScript)).
                combineScript(new Script(combineScript)).
                reduceScript(new Script(reduceScript));
        aggStream = Stream.concat(aggStream, Stream.of(scriptAgg));

       // aggStream = Stream.concat(aggStream, Stream.of(AggregationBuilders.avg("cost_total_amount").field("cost_total_amount")));
        //聚合查询
        AbstractAggregationBuilder agg = ESQueryUtils.tansformGroup(aggStream);

        //执行查询
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withIndices(index)
                .withTypes("outp_charge_info")
                .addAggregation(agg)
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
        logger.info("门急诊均次费用指标接口/rest/avgExpense/inpAvgExpense 结果:"+result);
        return result;
    }
}
