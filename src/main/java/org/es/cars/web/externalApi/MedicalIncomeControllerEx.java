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
import java.util.Map;

/**
 * Created by liuyoucai on 2017/4/27.
 * 医疗收入报表
 */
@Controller
@RequestMapping(value = "/rest/medicalInEx")
public class MedicalIncomeControllerEx {
    Logger logger  =  LoggerFactory.getLogger(MedicalIncomeControllerEx.class );
    @Value("${ip.address}")
    private String ipAddress;

    @SuppressWarnings("unchecked")
    @RequestMapping("outpIncome")
    @ResponseBody
    @POST
    /**
     * 门急诊总收入
     * 维度：时间、医院、科室
     */
    public String getOutpIncome(@RequestBody String params) throws UnsupportedEncodingException {
        //获取解析参数
        String originParam = URLDecoder.decode(params,"utf-8").substring(0,URLDecoder.decode(params,"utf-8").length()-1);
        logger.info("门急诊总收入报表接口/rest/medicalInEx/outpIncome 参数:"+originParam);
        Map<String, String> map = JSONArray.parseObject(originParam, Map.class);

        String year = map.get("year");
        String month = map.get("month");
        String department = map.get("department");
        String feetype = map.get("feetype");
        String startDate = "";
        String endDate = "";
        ESQueryGroup group = null ;
        if (month == null || "".equals(month)) {
            startDate = year + "-01-01";
            endDate = DateUtils.dateToString(DateUtils.getNextYearDate(DateUtils.stringToDate(year, "yyyy")), "yyyy-MM-dd");
            group = new ESDateGroup("cost_date","month","MM");
        } else {
            startDate = year + "-" + month + "-01";
            endDate = DateUtils.dateToString(DateUtils.getNextMonthDate(DateUtils.stringToDate(year + month, "yyyyMM")), "yyyy-MM-dd");
            group =  new ESDateGroup("cost_date","day","dd");
        }
        //构建指标接口参数对象
        ESParam param = new ESParam();
        if(!"".equals(startDate)) {
            ESQueryFilter filter = new ESQueryFilter("cost_date", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);
            param.addFilter(filter);
            param.setGroup(group);
        }
        if(department!=null && !"".equals(department))
        {
            ESQueryFilter filter = new ESQueryFilter("dept_name", ESQueryFilter.FilterType.MATCH, department);
            param.addFilter(filter);
        }
        if(feetype!=null && !"".equals(feetype))
        {
            ESQueryFilter filter = new ESQueryFilter("cost_type_name", ESQueryFilter.FilterType.MATCH, feetype);
            param.addFilter(filter);
        }

        String jsonStr=JSON.toJSONString(param);

        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/medicalIn/outpIncome", jsonStr, "utf-8");
        String result = JSON.toJSON(Result.ok(ParamsUtils.pareseToHistogram(json,"cost_date"))).toString();
        logger.info("门急诊总收入报表接口/rest/medicalInEx/outpIncome 结果:"+result);
        return result;

    }
    @SuppressWarnings("unchecked")
    @RequestMapping("dischargeIncome")
    @ResponseBody
    @POST
    /**
     * 出院总收入
     * 维度：时间、医院、科室
     */
    public String getDischargeIncome(@RequestBody String params) throws UnsupportedEncodingException {
        //获取解析参数
        String originParam = URLDecoder.decode(params,"utf-8").substring(0,URLDecoder.decode(params,"utf-8").length()-1);
        logger.info("出院总收入报表接口/rest/medicalInEx/dischargeIncome 参数:"+originParam);
        Map<String, String> map = JSONArray.parseObject(originParam, Map.class);

        String year = map.get("year");
        String month = map.get("month");
        String department = map.get("department");
        String feetype = map.get("feetype");
        String startDate = "";
        String endDate = "";
        ESQueryGroup group = null ;
        if (month == null || "".equals(month)) {
            startDate = year + "-01-01";
            endDate = DateUtils.dateToString(DateUtils.getNextYearDate(DateUtils.stringToDate(year, "yyyy")), "yyyy-MM-dd");
            group = new ESDateGroup("cost_date","month","MM");
        } else {
            startDate = year + "-" + month + "-01";
            endDate = DateUtils.dateToString(DateUtils.getNextMonthDate(DateUtils.stringToDate(year + month, "yyyyMM")), "yyyy-MM-dd");
            group =  new ESDateGroup("cost_date", "day","dd");
        }
        //构建指标接口参数对象
        ESParam param = new ESParam();
        if(!"".equals(startDate)) {
            ESQueryFilter filter = new ESQueryFilter("cost_date", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);
            param.addFilter(filter);
            param.setGroup(group);
        }
        if(department!=null && !"".equals(department))
        {
            ESQueryFilter filter = new ESQueryFilter("dept_name", ESQueryFilter.FilterType.MATCH, department);
            param.addFilter(filter);
        }
        if(feetype!=null && !"".equals(feetype))
        {
            ESQueryFilter filter = new ESQueryFilter("cost_type_name", ESQueryFilter.FilterType.MATCH, feetype);
            param.addFilter(filter);
        }
        String jsonStr=JSON.toJSONString(param);

        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/medicalIn/dischargeIncome", jsonStr, "utf-8");
        String result= JSON.toJSON(Result.ok(ParamsUtils.pareseToHistogram(json,"cost_date"))).toString();
        logger.info("出院总收入报表接口/rest/medicalInEx/dischargeIncome 结果:"+result);
        return result;

    }
}
