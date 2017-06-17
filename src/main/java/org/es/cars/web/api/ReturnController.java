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
@RequestMapping(value = "/rest/return")
/**
 * Created by zhangw on 2017/6/5.
 * 重返类指标
 */
public class ReturnController extends BaseController{
    Logger logger  =  LoggerFactory.getLogger(ReturnController.class );

    ////初始化公共变量
    String dimentions="dishospital_date,inp_dept_code,inp_dept_name,area_name,sex_name,age_year";
    String childDims="org_id,diag_code,diag_name";
    ESQuery esQuery=new ESQuery(Arrays.asList(dimentions.split(",")),
            Arrays.asList(childDims.split(",")),
            "inp_rec_inp_mr_page",
            "inp_mr_diag");

    //出院重返比例聚合
    //?
    //TODO
    String initScript="params._agg.death = new HashSet();params._agg.nums = new HashSet()";
    String mapScript="if(doc.features_type_code.value == \"03\" && doc.treat_situation.value == \"死亡\") params._agg.death.add(doc.health_event_id.value); params._agg.nums.add(doc.health_event_id.value)";
    String combineScript="Set death=new HashSet();Set nums=new HashSet();for (t in params._agg.death) { death.add(t) } for (t in params._agg.nums) { nums.add(t) } return [death,nums]";
    String reduceScript="Set death=new HashSet();Set nums=new HashSet(); for (a in params._aggs) if(a!=null) { death.addAll(a[0]);nums.addAll(a[1]) }  return nums.size() == 0 ? 0d : death.size()*1.0/nums.size()";
    AbstractAggregationBuilder returnRateAgg= AggregationBuilders.scriptedMetric("inpAvgExpense").
            initScript(new Script(initScript)).
            mapScript(new Script(mapScript)).
            combineScript(new Script(combineScript)).
            reduceScript(new Script(reduceScript));

    @SuppressWarnings("unchecked")
    @RequestMapping("returnRate")
    @ResponseBody
    @POST
    /**
     * 住院总重返率
     * 维度：时间、医院、科室
     */
    public String getReturnRate(@RequestBody String body) {
        logger.info("住院总重返率指标接口/rest/return/returnRate 参数:"+body);
        String result=dealAllHasChildType(body,esQuery,returnRateAgg);
        logger.info("住院总重返率指标接口/rest/return/returnRate 结果:"+result);
        return result;
    }



}
