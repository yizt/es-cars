package org.es.cars.examples;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.es.framework.util.json.ResultSet;
import org.es.framework.utils.ESQueryUtils;
import org.es.cars.es.model.Accident;
import org.es.cars.web.api.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.POST;
import java.util.List;
import java.util.Map;

/**
 * Created by mick.yi on 2017/6/16.
 * restful接口例子
 */
@Controller
@Configuration
@RequestMapping(value = "/rest/example")
public class RestFullExampleController extends BaseController {
    Logger logger = LoggerFactory.getLogger(RestFullExampleController.class);

    @SuppressWarnings("unchecked")
    @RequestMapping("getCount")
    @ResponseBody
    @POST
    /**
     * 词频统计
     */
    public String getCount(@RequestBody String body) {
        AbstractAggregationBuilder agg=AggregationBuilders.terms("word").field("content").size(100);
        String result = dealAllSingleType("accident",body,agg);
        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("accidentDtl")
    @ResponseBody
    @POST
    /**
     * 事故明细
     */
    public String getAccidentDetail(@RequestBody String body) {
        Map<String, List<Map<Object, Object>>> map = JSONArray.parseObject(body, Map.class);
        List<Map<Object, Object>> filter = map.get("filter");
        //过滤条件
        AbstractQueryBuilder queryBuilder = ESQueryUtils.getQueryBuilder(filter);

        SearchQuery searchQuery = new NativeSearchQueryBuilder().
                withIndices(index).
                withTypes("accident").
                withQuery(queryBuilder).
                build();
        Page<Accident> pg = elasticsearchTemplate.queryForPage(searchQuery, Accident.class);
        String result = JSON.toJSONString(ResultSet.ok(pg.getContent()));
        return result;
    }

}