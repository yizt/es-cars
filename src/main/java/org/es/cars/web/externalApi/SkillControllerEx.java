package org.es.cars.web.externalApi;

/**
 * Created by liuyoucai on 2017/4/28.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.es.framework.util.json.Result;
import org.es.framework.utils.DateUtils;
import org.es.framework.utils.ParamsUtils;
import org.es.framework.utils.RESTFulClient;
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
 * Created by liuyoucai on 2017/4/24.
 * 检查检验接口
 */
@Controller
@RequestMapping(value = "/rest/skillEx")
public class SkillControllerEx {
    Logger logger  =  LoggerFactory.getLogger(SkillControllerEx.class );
    @Value("${ip.address}")
    private String ipAddress;
    @SuppressWarnings("unchecked")
    @RequestMapping("labInfo")
    @ResponseBody
    @POST
    /**
     * 获取检验数
     * 维度包括：
     * 时间：lab_date（检验日期）、
     * 医院：org_id、
     * 科室：dept_name、
     * 性别：patient_sex_name、
     * 年龄：patient_age_year
     *
     */
    public String getLabCount(@RequestBody String params) throws UnsupportedEncodingException {

        //获取解析参数
        String originParam = URLDecoder.decode(params,"utf-8").substring(0,URLDecoder.decode(params,"utf-8").length()-1);
        logger.info("检验报表接口/rest/skillEx/labInfo 参数:"+originParam);
        Map<String, String> map = JSONArray.parseObject(originParam, Map.class);

        String year = map.get("year");
        String month = map.get("month");
        String department = map.get("department");
        String startDate = "";
        String endDate = "";
        //ESQueryGroup group = null ;
        if (month == null || "".equals(month)) {
            startDate = year + "-01-01";
            endDate = DateUtils.dateToString(DateUtils.getNextYearDate(DateUtils.stringToDate(year, "yyyy")), "yyyy-MM-dd");
          //  group = new ESDateGroup("lab_date","month","MM");
        } else {
            startDate = year + "-" + month + "-01";
            endDate = DateUtils.dateToString(DateUtils.getNextMonthDate(DateUtils.stringToDate(year + month, "yyyyMM")), "yyyy-MM-dd");
           // group =  new ESDateGroup("lab_date","day","dd");
        }
        //构建指标接口参数对象
        ESParam param = new ESParam();
        if(!"".equals(startDate)) {
            ESQueryFilter filter = new ESQueryFilter("lab_date", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);
            param.addFilter(filter);
        }
        if(department!=null && !"".equals(department))
        {
            ESQueryFilter filter = new ESQueryFilter("dept_name", ESQueryFilter.FilterType.MATCH, department);
            param.addFilter(filter);
        }
        //添加分组
        ESQueryGroup lab_item_group = new ESQueryGroup("lab_item_name", ESQueryGroup.GroupType.LIST);
        param.setGroup(lab_item_group);

        String jsonStr=JSON.toJSONString(param);
        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/skill/labInfo", jsonStr, "utf-8");
        String result = JSON.toJSON(Result.ok(ParamsUtils.pareseToHistogram(json,"lab_item_name"))).toString();
        logger.info("检验报表接口/rest/skillEx/labInfo 结果:"+result);
        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("examInfo")
    @ResponseBody
    @POST
    /**
     * 获取检查数
     * 维度包括：
     * 时间：exam_date（检查日期）、
     * 医院：org_id、
     * 科室：dept_name、
     * 性别：patient_sex_name、
     * 年龄：patient_age_year
     *
     */
    public String getExamCount(@RequestBody String params) throws UnsupportedEncodingException {
        //获取解析参数
        String originParam = URLDecoder.decode(params,"utf-8").substring(0,URLDecoder.decode(params,"utf-8").length()-1);
        logger.info("检查报表接口/rest/skillEx/examInfo 参数:"+originParam);
        Map<String, String> map = JSONArray.parseObject(originParam, Map.class);

        String year = map.get("year");
        String month = map.get("month");
        String department = map.get("department");
        String startDate = "";
        String endDate = "";
        //ESQueryGroup group = null ;
        if (month == null || "".equals(month)) {
            startDate = year + "-01-01";
            endDate = DateUtils.dateToString(DateUtils.getNextYearDate(DateUtils.stringToDate(year, "yyyy")), "yyyy-MM-dd");
            //group = new ESDateGroup("exam_date", "month","MM");
        } else {
            startDate = year + "-" + month + "-01";
            endDate = DateUtils.dateToString(DateUtils.getNextMonthDate(DateUtils.stringToDate(year + month, "yyyyMM")), "yyyy-MM-dd");
           // group =  new ESDateGroup("exam_date","day","dd");
        }
        //构建指标接口参数对象
        ESParam param = new ESParam();
        if(!"".equals(startDate)) {
            ESQueryFilter filter = new ESQueryFilter("exam_date", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);
            param.addFilter(filter);
        }
        if(department!=null && !"".equals(department))
        {
            ESQueryFilter filter = new ESQueryFilter("dept_name", ESQueryFilter.FilterType.MATCH, department);
            param.addFilter(filter);
        }
        //添加分组
        ESQueryGroup lab_item_group = new ESQueryGroup("exam_method_name", ESQueryGroup.GroupType.LIST);
        param.setGroup(lab_item_group);
        String jsonStr=JSON.toJSONString(param);

        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/skill/examInfo", jsonStr, "utf-8");
        String result = JSON.toJSON(Result.ok(ParamsUtils.pareseToHistogram(json,"exam_method_name"))).toString();
        logger.info("检查报表接口/rest/skillEx/examInfo 结果:"+result);
        return result;
    }

}
