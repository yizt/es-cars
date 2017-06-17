package org.es.cars.web.api;


import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.es.cars.es.model.ESQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.POST;
import java.util.Arrays;

@Controller
@Configuration
@RequestMapping(value = "/rest/complication")
/**
 * Created by zhangw on 2017/6/12.
 * 手术并发症类指标
 */
public class ComplicationController extends BaseController{
    Logger logger  =  LoggerFactory.getLogger(ComplicationController.class );

    ////初始化公共变量
    String dimentions="dishospital_date,inp_dept_code,inp_dept_name,area_name,sex_name,age_year";
    String childDims="org_id,diag_code,diag_name";
    ESQuery esQuery=new ESQuery(Arrays.asList(dimentions.split(",")),
            Arrays.asList(childDims.split(",")),
            "inp_rec_inp_mr_page",
            "inp_mr_diag");

    //手术并发症比例聚合
    String initScript="params._agg.infections = new HashSet();params._agg.nums = new HashSet()";
    String mapScript="if(doc.features_type_code.value == \"09\") params._agg.infections.add(doc.health_event_id.value); params._agg.nums.add(doc.health_event_id.value)";
    String combineScript="Set infects=new HashSet();Set nums=new HashSet();for (t in params._agg.infections) { infects.add(t) } for (t in params._agg.nums) { nums.add(t) } return [infects,nums]";
    String reduceScript="Set infects=new HashSet();Set nums=new HashSet(); for (a in params._aggs) if(a!=null) { infects.addAll(a[0]);nums.addAll(a[1]) }  return nums.size() == 0 ? 0d : infects.size()*1.0/nums.size()";
    AbstractAggregationBuilder complicationRateAgg= AggregationBuilders.scriptedMetric("inpAvgExpense").
            initScript(new Script(initScript)).
            mapScript(new Script(mapScript)).
            combineScript(new Script(combineScript)).
            reduceScript(new Script(reduceScript));

    @SuppressWarnings("unchecked")
    @RequestMapping("complicationRate")
    @ResponseBody
    @POST
    /**
     * 住院总手术并发症
     * 维度：时间、医院、科室
     */
    public String getComplicationRate(@RequestBody String body) {
        logger.info("住院总手术并发症发生率指标接口/rest/complication/complicationRate 参数:" + body);
        String result = dealAllHasChildType(body, esQuery, complicationRateAgg);
        logger.info("住院总手术并发症发生率指标接口/rest/complication/complicationRate 结果:" + result);
        return result;
    }






}
