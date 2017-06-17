package org.es.cars.web.api;

import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.POST;


@Controller
@Configuration
@RequestMapping(value = "/rest/drug")
/**
 * Created by zhangw on 2017/6/12.
 * 6.医疗机构合理用药指标
 */
public class DrugController extends BaseController{
    Logger logger  =  LoggerFactory.getLogger(DrugController.class );


    @SuppressWarnings("unchecked")
    @RequestMapping("outpDrugFee")
    @ResponseBody
    @POST
    /**
     * 门诊药品收入
     * 维度：时间、医院、科室、费用项
     */
    public String getOutpDrugFee(@RequestBody String body) {
        logger.info("门诊药品收入指标接口/rest/drug/outpDrugFee 参数:"+body);
        AbstractQueryBuilder filterBuilder =  QueryBuilders.termQuery("is_drug", "1");
        AbstractAggregationBuilder outpDrugAgg = AggregationBuilders.sum("cost_total_amount").field("cost_total_amount");
        String result=dealAllSingleType("outp_charge_info",body,filterBuilder,outpDrugAgg);
        logger.info("门诊药品收入指标接口/rest/drug/outpDrugFee 结果:"+result);
        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("inpDrugFee")
    @ResponseBody
    @POST
    /**
     * 住院药品收入
     * 维度：时间、医院、科室、费用项
     */
    public String getInpDrugFee(@RequestBody String body) {
        logger.info("住院药品收入指标接口/rest/drug/outpDrugFee 参数:"+body);
        AbstractQueryBuilder filterBuilder =  QueryBuilders.termQuery("is_drugs", "1");
        AbstractAggregationBuilder aggBuilder = AggregationBuilders.sum("cost_money").field("cost_money");
        String result=dealAllSingleType("inp_bill_detail",body,filterBuilder,aggBuilder);
        logger.info("住院药品收入指标接口/rest/drug/outpDrugFee 结果:"+result);
        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("outpRecipeNum")
    @ResponseBody
    @POST
    /**
     * 门诊处方总数
     * 维度：时间、医院、科室
     */
    public String getOutpRecipeNum(@RequestBody String body) {
        logger.info("门诊处方总数指标接口/rest/drug/outpRecipeNum 参数:"+body);
        String result=dealAllSingleType("outp_recipe_drug",body,null,null);
        logger.info("门诊处方总数指标接口/rest/drug/outpRecipeNum 结果:"+result);
        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("outpInjectRecipeNum")
    @ResponseBody
    @POST
    /**
     * 门诊含注射剂处方数
     * 维度：时间、医院、科室
     */
    public String getOutpInjectRecipeNum(@RequestBody String body) {
        logger.info("门诊处方总数指标接口/rest/drug/outpInjectRecipeNum 参数:"+body);
        AbstractQueryBuilder filterBuilder =  QueryBuilders.wildcardQuery("drug_name", "*注射剂*");
        String result=dealAllSingleType("outp_recipe_drug",body,filterBuilder,null);
        logger.info("门诊处方总数指标接口/rest/drug/outpInjectRecipeNum 结果:"+result);
        return result;
    }

    String initScript="params._agg.idlist = new HashSet();params._agg.nums = []";
    String mapScript="params._agg.idlist.add(doc.health_event_id.value);params._agg.nums.add(1)";
    String combineScript="double nums=0; for (t in params._agg.nums) { nums+=t } return [params._agg.idlist,nums]";
    String reduceScript="Set totalSet=new HashSet();double nums=0; for (a in params._aggs) if(a!=null) {totalSet.addAll(a[0]); nums+=a[1] }  return totalSet.size() == 0 ? 0d : totalSet.size()/nums*1.0";
    AbstractAggregationBuilder scriptAgg= AggregationBuilders.scriptedMetric("outpAvgExpense").
            initScript(new Script(initScript)).
            mapScript(new Script(mapScript)).
            combineScript(new Script(combineScript)).
            reduceScript(new Script(reduceScript));


    @SuppressWarnings("unchecked")
    @RequestMapping("avgDrugNum")
    @ResponseBody
    @POST
    /**
     * 每次就诊人均用药品种数
     * 维度：时间、医院、科室
     */
    public String getAvgDrugNum(@RequestBody String body) {
        logger.info("每次就诊人均用药品种数指标接口/rest/drug/avgDrugNum 参数:"+body);
        String result=dealAllSingleType("outp_recipe_drug",body,null,scriptAgg);
        logger.info("每次就诊人均用药品种数指标接口/rest/drug/avgDrugNum 结果:"+result);
        return result;
    }


    //每次就诊人均药费
    String druginitScript="params._agg.idlist = new HashSet();params._agg.nums = []";
    String drugmapScript="if(doc.is_drug.value == \"1\") params._agg.idlist.add(doc.health_event_id.value);params._agg.nums.add(doc.cost_total_amount.value == \" \" ? 0 : doc.cost_total_amount.value)";
    String drugcombineScript="double nums=0; for (t in params._agg.nums) { nums+=t } return [params._agg.idlist,nums]";
    String drugreduceScript="Set totalSet=new HashSet();double nums=0; for (a in params._aggs) if(a!=null) {totalSet.addAll(a[0]); nums+=a[1] }  return totalSet.size() == 0 ? 0d : nums/totalSet.size()*1.0";
    AbstractAggregationBuilder drugscriptAgg= AggregationBuilders.scriptedMetric("inpAvgExpense").
            initScript(new Script(druginitScript)).
            mapScript(new Script(drugmapScript)).
            combineScript(new Script(drugcombineScript)).
            reduceScript(new Script(drugreduceScript));

    @SuppressWarnings("unchecked")
    @RequestMapping("avgDrugExpense")
    @ResponseBody
    @POST
    /**
     * 每次就诊人均药费
     * 维度：时间、医院、科室
     */
    public String getAvgDrugExpense(@RequestBody String body) {
        logger.info("每次就诊人均药费指标接口/rest/drug/avgDrugExpense 参数:"+body);
        String result=dealAllSingleType("outp_charge_info",body,null,drugscriptAgg);
        logger.info("每次就诊人均药费指标接口/rest/drug/avgDrugExpense 结果:"+result);
        return result;
    }






}
