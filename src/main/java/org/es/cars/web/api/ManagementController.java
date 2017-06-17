package org.es.cars.web.api;

import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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
@RequestMapping(value = "/rest/management")
/**
 * Created by zhangw on 2017/6/13.
 * 医院运行管理类指标
 */
public class ManagementController extends BaseController {

    Logger logger  =  LoggerFactory.getLogger(ManagementController.class );

    ////初始化公共变量
    String dimentions="dishospital_date,inp_dept_code,inp_dept_name,area_name,sex_name,age_year";
    String childDims="org_id,diag_code,diag_name";
    ESQuery esQuery=new ESQuery(Arrays.asList(dimentions.split(",")),
            Arrays.asList(childDims.split(",")),
            "inp_rec_inp_mr_page",
            "inp_mr_diag");

    //医院感染比例聚合
    String initScript="params._agg.infections = new HashSet();params._agg.nums = new HashSet()";
    String combineScript="Set infects=new HashSet();Set nums=new HashSet();for (t in params._agg.infections) { infects.add(t) } for (t in params._agg.nums) { nums.add(t) } return [infects,nums]";
    String reduceScript="Set infects=new HashSet();Set nums=new HashSet(); for (a in params._aggs) if(a!=null) { infects.addAll(a[0]);nums.addAll(a[1]) }  return nums.size() == 0 ? 0d : infects.size()*1.0/nums.size()";
    String cureMapScript="if(doc.features_type_code.value == \"03\" && (doc.treat_situation.value == \"治愈\" || doc.treat_situation.value == \"其他\")) params._agg.infections.add(doc.health_event_id.value); params._agg.nums.add(doc.health_event_id.value)";
    String betterMapScript="if(doc.features_type_code.value == \"03\" && doc.treat_situation.value == \"好转\") params._agg.infections.add(doc.health_event_id.value); params._agg.nums.add(doc.health_event_id.value)";
    String deathMapScript="if(doc.features_type_code.value == \"03\" && doc.treat_situation.value == \"死亡\") params._agg.infections.add(doc.health_event_id.value); params._agg.nums.add(doc.health_event_id.value)";

    AbstractAggregationBuilder cureRateAgg= AggregationBuilders.scriptedMetric("inpAvgExpense").
            initScript(new Script(initScript)).
            mapScript(new Script(cureMapScript)).
            combineScript(new Script(betterMapScript)).
            reduceScript(new Script(reduceScript));

    AbstractAggregationBuilder betterRateAgg= AggregationBuilders.scriptedMetric("inpAvgExpense").
            initScript(new Script(initScript)).
            mapScript(new Script(deathMapScript)).
            combineScript(new Script(betterMapScript)).
            reduceScript(new Script(reduceScript));
    AbstractAggregationBuilder deathRateAgg= AggregationBuilders.scriptedMetric("inpAvgExpense").
            initScript(new Script(initScript)).
            mapScript(new Script(deathMapScript)).
            combineScript(new Script(combineScript)).
            reduceScript(new Script(reduceScript));

    @SuppressWarnings("unchecked")
    @RequestMapping("inpBedDays")
    @ResponseBody
    @POST
    /**
     * 出院患者实际占用总床日
     * 维度：时间、医院、科室
     */
    public String getOutpMRnum(@RequestBody String body) {
        logger.info("出院患者实际占用总床日指标接口/rest/management/inpBedDays 参数:"+body);
        AbstractAggregationBuilder aggBuilder = AggregationBuilders.sum("inp_days").field("inp_days");
        String result=dealAllSingleType("dishospital_summary",body,null,aggBuilder);
        logger.info("出院患者实际占用总床日指标接口/rest/management/inpBedDays 结果:"+result);
        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("inpOperationNum")
    @ResponseBody
    @POST
    /**
     * 年住院手术例数
     * 维度：时间、医院、科室
     */
    public String getInpOperationNum(@RequestBody String body) {
        logger.info("年住院手术例数指标接口/rest/management/inpOperationNum 参数:"+body);
        String result=dealAllSingleType("general_operation_record",body,null,null);
        logger.info("年住院手术例数指标接口/rest/management/inpOperationNum 结果:"+result);
        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("CtNum")
    @ResponseBody
    @POST
    /**
     * CT检查人次数
     * 维度：时间、医院、科室
     */
    public String getCTNum(@RequestBody String body) {
        logger.info("CT检查人次数指标接口/rest/management/CtNum 参数:"+body);
        AbstractQueryBuilder filterBuilder =  QueryBuilders.wildcardQuery("exam_method_name", "*CT*");
        String result=dealAllSingleType("exam_info",body,filterBuilder,null);
        logger.info("CT检查人次数指标接口/rest/management/CtNum 结果:"+result);
        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("MriNum")
    @ResponseBody
    @POST
    /**
     * MRI检查人次数
     * 维度：时间、医院、科室
     */
    public String getMRINum(@RequestBody String body) {
        logger.info("MRI检查人次数指标接口/rest/management/MriNum 参数:"+body);
        AbstractQueryBuilder filterBuilder =  QueryBuilders.wildcardQuery("exam_method_name", "*磁共振*");
        String result=dealAllSingleType("exam_info",body,filterBuilder,null);
        logger.info("MRI检查人次数指标接口/rest/management/MriNum 结果:"+result);
        return result;
    }


    @SuppressWarnings("unchecked")
    @RequestMapping("inpCureRate")
    @ResponseBody
    @POST
    /**
     * 治愈率
     * 维度：时间、医院、科室
     */
    public String getInpCureRate(@RequestBody String body) {
        logger.info("治愈率指标接口/rest/management/inpCureRate 参数:" + body);
        String result = dealAllHasChildType(body, esQuery, cureRateAgg);
        logger.info("治愈率指标接口/rest/management/inpCureRate 结果:" + result);
        return result;
    }
    @SuppressWarnings("unchecked")
    @RequestMapping("inpBetterRate")
    @ResponseBody
    @POST
    /**
     * 好转率
     * 维度：时间、医院、科室
     */
    public String getInpBetterRate(@RequestBody String body) {
        logger.info("好转率指标接口/rest/management/inpBetterRate 参数:" + body);
        String result = dealAllHasChildType(body, esQuery, betterRateAgg);
        logger.info("好转率指标接口/rest/management/inpBetterRate 结果:" + result);
        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("inpDeathRate")
    @ResponseBody
    @POST
    /**
     * 住院病死率
     * 维度：时间、医院、科室
     */
    public String getInpDeathRate(@RequestBody String body) {
        logger.info("住院病死率指标接口/rest/management/inpDeathRate 参数:" + body);
        String result = dealAllHasChildType(body, esQuery, deathRateAgg);
        logger.info("住院病死率指标接口/rest/management/inpDeathRate 结果:" + result);
        return result;
    }

}
