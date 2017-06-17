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
 * 均次费用接口类
 */
@Controller
@RequestMapping(value = "/rest/avgExpenseEx")

public class AvgExpensesControllerEx {
    Logger logger  =  LoggerFactory.getLogger(AvgExpensesControllerEx.class );
    @Value("${ip.address}")
    private String ipAddress;

    @SuppressWarnings("unchecked")
    @RequestMapping("outpAvgExpense")
    @ResponseBody
    @POST
    /**
     * 门诊均次费用
     * 维度：时间、医院、科室、病种、性别、年龄、住院天数、医保、非医保
     */
    public String getutpAvgExpense(@RequestBody String params) throws UnsupportedEncodingException {

        //获取解析参数
        String originParam = URLDecoder.decode(params,"utf-8").substring(0,URLDecoder.decode(params,"utf-8").length()-1);
        logger.info("门诊均次费用报表接口/rest/avgExpenseEx/outpAvgExpense 参数:"+originParam);
        Map<String, String> map = JSONArray.parseObject(originParam, Map.class);

        String year = map.get("year");
        String month = map.get("month");
        String startDate = "";
        String endDate = "";
        ESDateGroup group = null ;
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
        ESQueryFilter filter = new ESQueryFilter("cost_date", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);
        ESParam param = new ESParam().setFilter(filter).setGroup(group);
        String jsonStr=JSON.toJSONString(param);
        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/avgExpense/outpAvgExpense", jsonStr, "utf-8");
        //返回结果
        String result = JSON.toJSON(Result.ok(ParamsUtils.pareseToHistogram(json,"cost_date"))).toString();
        logger.info("门诊均次费用报表接口/rest/avgExpenseEx/outpAvgExpense 结果:"+result);
        return result;
    }
    @SuppressWarnings("unchecked")
    @RequestMapping("inpAvgExpense")
    @ResponseBody
    @POST
    /**
     * 住院均次费用
     * 维度：时间、医院、科室、病种、性别、年龄、住院天数、医保、非医保
     */
    public String getInpAvgExpense(@RequestBody String params) throws UnsupportedEncodingException {
        //获取解析参数
        String originParam = URLDecoder.decode(params,"utf-8").substring(0,URLDecoder.decode(params,"utf-8").length()-1);
        logger.info("住院均次费用报表接口/rest/avgExpenseEx/inpAvgExpense 参数:"+originParam);
        Map<String, String> map = JSONArray.parseObject(originParam, Map.class);

        String year = map.get("year");
        String month = map.get("month");
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
        ESQueryFilter filter = new ESQueryFilter("cost_date", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);
        ESParam param = new ESParam().setFilter(filter).setGroup(group);
        String jsonStr=JSON.toJSONString(param);
        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/avgExpense/inpAvgExpense", jsonStr, "utf-8");
        //返回结果
        String result = JSON.toJSON(Result.ok(ParamsUtils.pareseToHistogram(json,"cost_date"))).toString();
        logger.info("住院均次费用报表接口/rest/avgExpenseEx/inpAvgExpense 结果:"+result);
        return result;
    }
}
