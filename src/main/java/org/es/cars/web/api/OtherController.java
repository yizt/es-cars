package org.es.cars.web.api;


import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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
@RequestMapping(value = "/rest/other")
/**
 * Created by zhangw on 2017/6/12.
 * 其他指标
 */
public class OtherController extends BaseController{
    Logger logger  =  LoggerFactory.getLogger(OtherController.class );
    @SuppressWarnings("unchecked")
    @RequestMapping("outpMRnum")
    @ResponseBody
    @POST
    /**
     * 门诊病案例数
     * 维度：时间、医院、科室
     */
    public String getOutpMRnum(@RequestBody String body) {
        logger.info("门诊病案例数指标接口/rest/drug/outpMRnum 参数:"+body);
        String result=dealAllSingleType("outp_mr",body,null,null);
        logger.info("门诊病案例数指标接口/rest/drug/outpMRnum 结果:"+result);
        return result;
    }


    @SuppressWarnings("unchecked")
    @RequestMapping("inpMRnum")
    @ResponseBody
    @POST
    /**
     * 住院病案例数
     * 维度：时间、医院、科室
     */
    public String getInpMRnum(@RequestBody String body) {
        logger.info("门诊病案例数指标接口/rest/drug/inpMRnum 参数:"+body);
        String result=dealAllSingleType("inp_mr_page",body,null,null);
        logger.info("门诊病案例数指标接口/rest/drug/inpMRnum 结果:"+result);
        return result;
    }


    @SuppressWarnings("unchecked")
    @RequestMapping("jiaMRnum")
    @ResponseBody
    @POST
    /**
     * 甲级病案例数
     * 维度：时间、医院、科室
     */
    public String getOutpDrugFee(@RequestBody String body) {
        logger.info("甲级病案例数指标接口/rest/drug/jiaMRnum 参数:"+body);
        AbstractQueryBuilder filterBuilder =  QueryBuilders.wildcardQuery("mr_quality_name", "*甲*");
        String result=dealAllSingleType("inp_mr_page",body,filterBuilder,null);
        logger.info("甲级病案例数指标接口/rest/drug/jiaMRnum 结果:"+result);
        return result;
    }

}
