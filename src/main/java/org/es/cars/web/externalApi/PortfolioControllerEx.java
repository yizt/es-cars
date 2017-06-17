package org.es.cars.web.externalApi;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.es.framework.util.json.Result;
import org.es.framework.utils.DateUtils;
import org.es.framework.utils.ParamsUtils;
import org.es.framework.utils.RESTFulClient;
import org.es.cars.es.model.ESDateGroup;
import org.es.cars.es.model.ESParam;
import org.es.cars.es.model.ESQueryFilter;
import org.es.cars.es.model.ESQueryGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.POST;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by liuyoucai on 2017/4/24.
 * 业务量指标外部接口
 */
@Controller
@RequestMapping(value = "/rest/portfolioEx")
public class PortfolioControllerEx {
    Logger logger  =  LoggerFactory.getLogger(PortfolioControllerEx.class );
    @Value("${ip.address}")
    private String ipAddress;
    @SuppressWarnings("unchecked")
    @RequestMapping("discharges")
    @ResponseBody
    @POST
    /**
     * 出院人次报表接口
     */
    public String getDischarges(@RequestBody String params ) throws UnsupportedEncodingException { //"year=2017&month=07&day=05"

        //获取解析参数
        String originParam = URLDecoder.decode(params,"utf-8").substring(0,URLDecoder.decode(params,"utf-8").length()-1);
        logger.info("出院人次报表接口/rest/portfolioEx/discharges 参数:"+originParam);
        Map<String, String> map = JSONArray.parseObject(originParam, Map.class);

        String year = map.get("year");
        String month = map.get("month");
        String startDate = "";
        String endDate = "";
        ESDateGroup group = null ;
        if (month == null || "".equals(month)) {
            startDate = year + "-01-01";
            endDate = DateUtils.dateToString(DateUtils.getNextYearDate(DateUtils.stringToDate(year, "yyyy")), "yyyy-MM-dd");
            group = new ESDateGroup("dishospital_date","month","MM");
        } else {
            startDate = year + "-" + month + "-01";
            endDate = DateUtils.dateToString(DateUtils.getNextMonthDate(DateUtils.stringToDate(year + month, "yyyyMM")), "yyyy-MM-dd");
            group =  new ESDateGroup("dishospital_date","day","dd");
        }
        //构建指标接口参数对象
        ESQueryFilter filter = new ESQueryFilter("dishospital_date", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);
        ESParam param = new ESParam().setFilter(filter).setGroup(group);
        String jsonStr=JSON.toJSONString(param);
        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/portfolio/discharges",jsonStr , "utf-8");
        //返回结果
        String result =JSON.toJSON(Result.ok(ParamsUtils.pareseToHistogram(json,"dishospital_date"))).toString();
        logger.info("出院人次报表接口/rest/portfolioEx/discharges 结果:"+result);
        return result;

    }
    @SuppressWarnings("unchecked")
    @RequestMapping("outMr")
    @ResponseBody
    @POST
    /**
     * 门急诊报表接口
     */
    public String getOutMr(@RequestBody String params) throws UnsupportedEncodingException
    {

        //获取解析参数
        String originParam = URLDecoder.decode(params,"utf-8").substring(0,URLDecoder.decode(params,"utf-8").length()-1);
        logger.info("门急诊报表接口/rest/portfolioEx/outMr 参数:"+originParam);
        Map<String, String> map = JSONArray.parseObject(originParam, Map.class);

        String year = map.get("year");
        String month = map.get("month");
        String startDate = "";
        String endDate = "";
        ESDateGroup group = null ;
        if (month == null || "".equals(month)) {
            startDate = year + "-01-01";
            endDate = DateUtils.dateToString(DateUtils.getNextYearDate(DateUtils.stringToDate(year, "yyyy")), "yyyy-MM-dd");
            group = new ESDateGroup("visit_datetime","month","MM");
        } else {
            startDate = year + "-" + month + "-01";
            endDate = DateUtils.dateToString(DateUtils.getNextMonthDate(DateUtils.stringToDate(year + month, "yyyyMM")), "yyyy-MM-dd");
            group =  new ESDateGroup("visit_datetime","day","dd");
        }
        //构建指标接口参数对象
        ESQueryFilter filter = new ESQueryFilter("visit_datetime", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);
        ESParam param = new ESParam().setFilter(filter).setGroup(group);
        String jsonStr=JSON.toJSONString(param);
        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/portfolio/outMr", jsonStr, "utf-8");
        //返回结果
        String result = JSON.toJSON(Result.ok(ParamsUtils.pareseToHistogram(json,"visit_datetime"))).toString();
        logger.info("门急诊报表接口/rest/portfolioEx/outMr 结果:"+result);
        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("inptime")
    @ResponseBody
    @POST
    /**
     * 住院人次报表接口
     */
    public String getInpTime(@RequestBody String params) throws UnsupportedEncodingException{

        //获取解析参数
        String originParam = URLDecoder.decode(params,"utf-8").substring(0,URLDecoder.decode(params,"utf-8").length()-1);
        logger.info("住院人次报表接口/rest/portfolioEx/inptime 参数:"+originParam);
        Map<String, String> map = JSONArray.parseObject(originParam, Map.class);

        String year = map.get("year");
        String month = map.get("month");
        String startDate = "";
        String endDate = "";
        ESQueryGroup group = null ;
        if (month == null || "".equals(month)) {
            startDate = year + "-01-01";
            endDate = DateUtils.dateToString(DateUtils.getNextYearDate(DateUtils.stringToDate(year, "yyyy")), "yyyy-MM-dd");
            group = new ESDateGroup("inp_date","month","MM");
        } else {
            startDate = year + "-" + month + "-01";
            endDate = DateUtils.dateToString(DateUtils.getNextMonthDate(DateUtils.stringToDate(year + month, "yyyyMM")), "yyyy-MM-dd");
            group =  new ESDateGroup("inp_date","day","dd");
        }
        //构建指标接口参数对象
        ESQueryFilter filter = new ESQueryFilter("inp_date", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);
        ESParam param = new ESParam().setFilter(filter).setGroup(group);
        String jsonStr=JSON.toJSONString(param);
        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/portfolio/inptime", jsonStr, "utf-8");
        //返回结果
        String result = JSON.toJSON(Result.ok(ParamsUtils.pareseToHistogram(json,"inp_date"))).toString();
        logger.info("住院人次报表接口/rest/portfolioEx/inptime 结果:"+result);
        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("outPatientAmount")
    @ResponseBody
    @POST
    /**
     * 门诊总量（年）
     */
    public String getOutPatientAmount(@RequestBody String params) throws UnsupportedEncodingException{
        //获取解析参数
        String originParam = URLDecoder.decode(params,"utf-8").substring(0,URLDecoder.decode(params,"utf-8").length()-1);
        logger.info("门诊总诊量报表接口/rest/portfolioEx/outPatientAmount 参数:"+originParam);
        Map<String, String> map = JSONArray.parseObject(originParam, Map.class);

        String year = map.get("year");
        String startDate = year + "-01-01";
        String endDate = DateUtils.dateToString(DateUtils.getNextYearDate(DateUtils.stringToDate(year, "yyyy")), "yyyy-MM-dd");
        //构建指标接口参数对象
        ESQueryFilter filter = new ESQueryFilter("visit_datetime", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);
        ESParam param = new ESParam().setFilter(filter).setGroup();
        String jsonStr=JSON.toJSONString(param);
        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/portfolio/outMr", jsonStr, "utf-8");
        //返回结果
        Map rs = JSONArray.parseObject(json, Map.class);
        Map<String,Object> _result = new HashMap<String,Object>();
        List<Map<Object,Object>> dataList = JSONArray.parseObject(rs.get("data").toString(), List.class);
        _result.put("result",dataList.get(0).get("value"));
        String result = JSON.toJSON(Result.ok(_result)).toString();
        logger.info("门诊总诊量报表接口/rest/portfolioEx/outPatientAmount 结果:"+result);
        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("expertsAmount")
    @ResponseBody
    @POST
    /**
     * 知名专家号（年）
     */
    public String getExpertsAmount(@RequestBody String params) throws UnsupportedEncodingException{
        //获取解析参数
        String originParam = URLDecoder.decode(params,"utf-8").substring(0,URLDecoder.decode(params,"utf-8").length()-1);
        logger.info("知名专家挂号数报表接口/rest/portfolioEx/expertsAmount 参数:"+originParam);
        Map<String, String> map = JSONArray.parseObject(originParam, Map.class);

        String year = map.get("year");
        String startDate = year + "-01-01";
        String endDate = DateUtils.dateToString(DateUtils.getNextYearDate(DateUtils.stringToDate(year, "yyyy")), "yyyy-MM-dd");
        //构建指标接口参数对象
        ESQueryFilter filter = new ESQueryFilter("visit_datetime", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);
        ESQueryFilter deptfilter = new ESQueryFilter("dept_name", ESQueryFilter.FilterType.MATCH, "专家");
        ESParam param = new ESParam().addFilter(filter).setGroup();
        param.addFilter(deptfilter);
        String jsonStr=JSON.toJSONString(param);
        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/portfolio/outMr", jsonStr, "utf-8");
        //返回结果
        Map rs = JSONArray.parseObject(json, Map.class);
        Map<String,Object> _result = new HashMap<String,Object>();
        List<Map<Object,Object>> dataList = JSONArray.parseObject(rs.get("data").toString(), List.class);
        _result.put("result",dataList.get(0).get("value"));
        String result = JSON.toJSON(Result.ok(_result)).toString();
        logger.info("知名专家挂号数报表接口/rest/portfolioEx/expertsAmount 结果:"+result);
        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("generalDocAmount")
    @ResponseBody
    @POST
    /**
     * 普通医师挂号数（年）
     */
    public String getGeneralDocAmount(@RequestBody String params) throws UnsupportedEncodingException{
        //获取解析参数
        String originParam = URLDecoder.decode(params,"utf-8").substring(0,URLDecoder.decode(params,"utf-8").length()-1);
        logger.info("普通医师挂号数报表接口/rest/portfolioEx/generalDocAmount 参数:"+originParam);
        Map<String, String> map = JSONArray.parseObject(originParam, Map.class);

        String year = map.get("year");
        String startDate = year + "-01-01";
        String endDate = DateUtils.dateToString(DateUtils.getNextYearDate(DateUtils.stringToDate(year, "yyyy")), "yyyy-MM-dd");
        //构建指标接口参数对象
        ESQueryFilter filter = new ESQueryFilter("visit_datetime", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);
        ESQueryFilter deptfilter = new ESQueryFilter("dept_name", ESQueryFilter.FilterType.REGEXP_MATCH,"~(.*ZJ.*)" );//^(?!.*hello)//"^(?!.*专家*)"

        ESParam param = new ESParam().addFilter(filter).setGroup();
        param.addFilter(deptfilter);
        String jsonStr=JSON.toJSONString(param);
        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/portfolio/outMr", jsonStr, "utf-8");
        //返回结果
        Map rs = JSONArray.parseObject(json, Map.class);
        Map<String,Object> _result = new HashMap<String,Object>();
        List<Map<Object,Object>> dataList = JSONArray.parseObject(rs.get("data").toString(), List.class);
        _result.put("result",dataList.get(0).get("value"));
        String result = JSON.toJSON(Result.ok(_result)).toString();
        logger.info("普通医师挂号数报表接口/rest/portfolioEx/generalDocAmount 结果:"+result);
        return result;
    }
}
