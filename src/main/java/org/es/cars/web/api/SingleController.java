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
@RequestMapping(value = "/rest/single")
/**
 * Created by zhangw on 2017/6/12.
 * 单病种质量指标
 */
public class SingleController extends BaseController{
    Logger logger  =  LoggerFactory.getLogger(SingleController.class );
    @SuppressWarnings("unchecked")
    @RequestMapping("xjgsNum")
    @ResponseBody
    @POST
    /**
     * 急性心肌梗死例数
     * 维度：时间、医院、科室
     */
    public String getXjgsNum(@RequestBody String body) {
        logger.info("急性心肌梗死例数指标接口/rest/single/xjgsNum 参数:"+body);
        AbstractQueryBuilder filterBuilder =  QueryBuilders.wildcardQuery("diag_name", "*急性心肌梗死*");
        String result=dealAllSingleType("inp_mr_diag",body,filterBuilder,null);
        logger.info("急性心肌梗死例数指标接口/rest/single/xjgsNum 结果:"+result);
        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("xlsjNum")
    @ResponseBody
    @POST
    /**
     * 心力衰竭例数
     * 维度：时间、医院、科室
     */
    public String getXlsjNum(@RequestBody String body) {
        logger.info("心力衰竭例数指标接口/rest/single/xlsjNum 参数:"+body);
        AbstractQueryBuilder filterBuilder =  QueryBuilders.wildcardQuery("diag_name", "*心力衰竭*");
        String result=dealAllSingleType("inp_mr_diag",body,filterBuilder,null);
        logger.info("心力衰竭例数指标接口/rest/single/xlsjNum 结果:"+result);
        return result;
    }
    @SuppressWarnings("unchecked")
    @RequestMapping("fyNum")
    @ResponseBody
    @POST
    /**
     * 心力衰竭例数
     * 维度：时间、医院、科室
     */
    public String getFyNum(@RequestBody String body) {
        logger.info("心力衰竭例数指标接口/rest/single/fyNum 参数:"+body);
        AbstractQueryBuilder filterBuilder =  QueryBuilders.wildcardQuery("diag_name", "*肺炎*");
        String result=dealAllSingleType("inp_mr_diag",body,filterBuilder,null);
        logger.info("心力衰竭例数指标接口/rest/single/fyNum 结果:"+result);
        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("ngsNum")
    @ResponseBody
    @POST
    /**
     * 急性脑梗死例数
     * 维度：时间、医院、科室
     */
    public String getNgsNum(@RequestBody String body) {
        logger.info("急性脑梗死例数指标接口/rest/single/ngsNum 参数:"+body);
        AbstractQueryBuilder filterBuilder =  QueryBuilders.wildcardQuery("diag_name", "*急性脑梗死*");
        String result=dealAllSingleType("inp_mr_diag",body,filterBuilder,null);
        logger.info("急性脑梗死例数指标接口/rest/single/ngsNum 结果:"+result);
        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("gzdmNum")
    @ResponseBody
    @POST
    /**
     * 冠状动脉旁路移植术例数
     * 维度：时间、医院、科室
     */
    public String getGzdmNum(@RequestBody String body) {
        logger.info("急性脑梗死例数指标接口/rest/single/gzdmNum 参数:"+body);
        AbstractQueryBuilder filterBuilder =  QueryBuilders.wildcardQuery("operation_code", "36.1*");
        String result=dealAllSingleType("general_operation_record",body,filterBuilder,null);
        logger.info("急性脑梗死例数指标接口/rest/single/gzdmNum 结果:"+result);
        return result;
    }



}


