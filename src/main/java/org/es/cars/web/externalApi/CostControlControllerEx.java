package org.es.cars.web.externalApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.es.framework.util.json.Result;
import org.es.framework.utils.DateUtils;
import org.es.framework.utils.RESTFulClient;
import org.es.cars.es.model.*;
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
import java.util.stream.Collectors;

/**
 * Created by mick.yi on 2017/4/27.
 * 医保控费类
 */
@Controller
@RequestMapping(value = "/rest/costControlEx")
public class CostControlControllerEx {
    Logger logger  =  LoggerFactory.getLogger(CostControlControllerEx.class );
    //精细化指标接口地址
    @Value("${ip.address}")
    private String ipAddress;

    @SuppressWarnings("unchecked")
    @RequestMapping("inpAvgTopDept")
    @ResponseBody
    @POST
    /**
     * 大项目各科室均次费用Top10
     * 每个费用类型在均次费用中top10科室
     * 此接口必须传入参数费用类型：cost_type或cost_type_name
     * 维度：时间、医院
     */
    public String getAvgFeeTopDept(@RequestBody String params) throws UnsupportedEncodingException {

        //获取解析参数
        String originParam = URLDecoder.decode(params, "utf-8").substring(0, URLDecoder.decode(params, "utf-8").length() - 1);
        logger.info("大项目各科室均次费用Top10报表/rest/costControlEx/inpAvgTopDept 参数:\n"+originParam);
        Map<String, String> map = JSONArray.parseObject(originParam, Map.class);

        String year = map.get("year");
        String month = map.get("month");
        String feetype = map.get("feetype");
        String startDate = "";
        String endDate = "";
        if (month == null || "".equals(month)) {
            startDate = year + "-01-01";
            endDate = DateUtils.dateToString(DateUtils.getNextYearDate(DateUtils.stringToDate(year, "yyyy")), "yyyy-MM-dd");
        } else {
            startDate = year + "-" + month + "-01";
            endDate = DateUtils.dateToString(DateUtils.getNextMonthDate(DateUtils.stringToDate(year + month, "yyyyMM")), "yyyy-MM-dd");
        }
        //构建指标接口参数对象
        ESParam param = new ESParam();
        if(!"".equals(startDate))
        {
            ESQueryFilter filter = new ESQueryFilter("cost_date", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);
            param.addFilter(filter);
        }
       if(feetype!=null && !"".equals(feetype))
       {
           ESQueryFilter filter = new ESQueryFilter("cost_type_name", ESQueryFilter.FilterType.MATCH, feetype);
           param.addFilter(filter);
       }
        ESQueryGroup group = new ESQueryGroup("dept_name", ESQueryGroup.GroupType.LIST);
        param.setGroup(group);

        String jsonStr = JSON.toJSONString(param);
        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/avgExpense/inpAvgExpense", jsonStr, "utf-8");
        //结果解析
        Map rs = JSONArray.parseObject(json, Map.class);
        List<Map<String, String>> dataList = JSONArray.parseObject(rs.get("data").toString(), List.class);
        //排序取top10
        dataList.sort((o1, o2) -> Double.valueOf(o2.get("value")).compareTo(Double.valueOf(o1.get("value"))));

        List<Map<String, String>> newList = new ArrayList<Map<String, String>>();
        for (int i = 0; i <= 10 && i < dataList.size(); i++) {
            newList.add(dataList.get(i));
        }
        String[][] resultData = newList.stream().
                map(elem -> new String[]{elem.get("dept_name").toString(), elem.get("value").toString()}).
                toArray(String[][]::new);
        String result=JSON.toJSONString(Result.ok(resultData));
        logger.info("大项目各科室均次费用Top10报表/rest/costControlEx/inpAvgTopDept 结果:\n"+result);
        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("inpAvgTopFeeItem")
    @ResponseBody
    @POST
    /**
     * 科室住院均次费用项目贡献度排行Top10
     * 维度：时间、医院
     */
    public String getAvgFeeTopFeeItem(@RequestBody String params) throws UnsupportedEncodingException {
        //获取解析参数
        String originParam = URLDecoder.decode(params, "utf-8").substring(0, URLDecoder.decode(params, "utf-8").length() - 1);
        logger.info("科室住院均次费用项目贡献度排行Top10报表/rest/costControlEx/inpAvgTopFeeItem 参数:\n"+originParam);
        Map<String, String> map = JSONArray.parseObject(originParam, Map.class);

        String year = map.get("year");
        String month = map.get("month");
        String department = map.get("department");
        String startDate = "";
        String endDate = "";
        if (month == null || "".equals(month)) {
            startDate = year + "-01-01";
            endDate = DateUtils.dateToString(DateUtils.getNextYearDate(DateUtils.stringToDate(year, "yyyy")), "yyyy-MM-dd");
        } else {
            startDate = year + "-" + month + "-01";
            endDate = DateUtils.dateToString(DateUtils.getNextMonthDate(DateUtils.stringToDate(year + month, "yyyyMM")), "yyyy-MM-dd");
        }

        //构建指标接口参数对象
        ESQueryFilter filter = new ESQueryFilter("cost_date", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);

        ESQueryGroup group = new ESQueryGroup("cost_item_name", ESQueryGroup.GroupType.LIST); //费用项目
        ESParam param = new ESParam().addFilter(filter).setGroup(group);
        if(department!=null && !"".equals(department))
        {
            ESQueryFilter deptfilter = new ESQueryFilter("dept_name", ESQueryFilter.FilterType.MATCH,department);
            param.addFilter(deptfilter);
        }
        String jsonStr = JSON.toJSONString(param);
        System.out.println(jsonStr);
        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/avgExpense/inpAvgExpense", jsonStr, "utf-8");
        //结果解析
        Map rs = JSONArray.parseObject(json, Map.class);
        List<Map<String, String>> dataList = JSONArray.parseObject(rs.get("data").toString(), List.class);
        //排序取top10
        dataList.sort((o1, o2) -> Double.valueOf(o2.get("value")).compareTo(Double.valueOf(o1.get("value"))));
        List<Map<String, String>> newList = new ArrayList<Map<String, String>>();
        for (int i = 0; i <= 10 && i < dataList.size(); i++) {
            newList.add(dataList.get(i));
        }
        String[][] resultData = newList.stream().
                map(elem -> new String[]{elem.get("cost_item_name").toString(), elem.get("value").toString()}).
                toArray(String[][]::new);
        String result=JSON.toJSONString(Result.ok(resultData));
        logger.info("科室住院均次费用项目贡献度排行Top10报表/rest/costControlEx/inpAvgTopFeeItem 结果:\n"+result);
        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("avgFeeYoYQoQ")
    @ResponseBody
    @POST
    /**
     * 住院均次费用同比环比
     * 维度：时间、医院
     */
    public String getAvgFeeYoYQoQ(@RequestBody String params) throws UnsupportedEncodingException {
        //获取解析参数
        String originParam = URLDecoder.decode(params, "utf-8").substring(0, URLDecoder.decode(params, "utf-8").length() - 1);
        logger.info("住院均次费用同比环比报表/rest/costControlEx/avgFeeYoYQoQ 参数:\n"+originParam);
        Map<String, String> map = JSONArray.parseObject(originParam, Map.class);
        String year = map.get("year");
        String month = map.get("month");
        String department = map.get("department");
        String feetype = map.get("feetype");
        if(month==null || "".equals(month))
            month="01";

        ////step1:查询当前参数月份
        String startDate = year + "-" + month + "-01";
        String endDate = DateUtils.dateToString(DateUtils.getNextMonthDate(DateUtils.stringToDate(year + month, "yyyyMM")), "yyyy-MM-dd");
        //构建指标接口参数对象
        ESQueryFilter deptfilter = null;
        ESQueryFilter feetypefilter = null;
        ESQueryFilter filter = new ESQueryFilter("cost_date", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);
        ESParam param = new ESParam().addFilter(filter).setGroup();
         if(department!=null && !"".equals(department))
         {
             deptfilter = new ESQueryFilter("dept_name", ESQueryFilter.FilterType.MATCH, department);
             param.addFilter(deptfilter);
         }
        if(feetype!=null && !"".equals(feetype))
        {
            feetypefilter = new ESQueryFilter("cost_type_name", ESQueryFilter.FilterType.MATCH, feetype);
            param.addFilter(feetypefilter);
        }

        String jsonStr = JSON.toJSONString(param);
        System.out.println(jsonStr);
        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/avgExpense/inpAvgExpense", jsonStr, "utf-8");
        //结果解析
        Map rs = JSONArray.parseObject(json, Map.class);
        List<Map<String, String>> dataList = JSONArray.parseObject(rs.get("data").toString(), List.class);

        ////step2:获取同比的值
        String yoyStartDate = DateUtils.dateToString(DateUtils.getLastYearDate(DateUtils.stringToDate(startDate, "yyyy-MM-dd")), "yyyy-MM-dd");
        String yoyEndDate = DateUtils.dateToString(DateUtils.getNextMonthDate(DateUtils.stringToDate(yoyStartDate, "yyyy-MM-dd")), "yyyy-MM-dd");
        filter = new ESQueryFilter("cost_date", ESQueryFilter.FilterType.RANGE, yoyStartDate + ";" + yoyEndDate);
        param = new ESParam().addFilter(filter).setGroup();
        if(deptfilter!=null)
        {
            param.addFilter(deptfilter);
        }
        if(feetypefilter !=null)
        {
            param.addFilter(feetypefilter);
        }
        jsonStr = JSON.toJSONString(param);
        System.out.println(jsonStr);
        json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/avgExpense/inpAvgExpense", jsonStr, "utf-8");
        //结果解析
        rs = JSONArray.parseObject(json, Map.class);
        List<Map<String, String>> yoyDataList = JSONArray.parseObject(rs.get("data").toString(), List.class);

        ////step3:获取环比的值
        String qoqStartDate = DateUtils.dateToString(DateUtils.getLastMonthDate(DateUtils.stringToDate(startDate, "yyyy-MM-dd")), "yyyy-MM-dd");
        String qoqEndDate = DateUtils.dateToString(DateUtils.getNextMonthDate(DateUtils.stringToDate(qoqStartDate, "yyyy-MM-dd")), "yyyy-MM-dd");
        filter = new ESQueryFilter("cost_date", ESQueryFilter.FilterType.RANGE, qoqStartDate + ";" + qoqEndDate);
        param = new ESParam().addFilter(filter).setGroup();
        if(deptfilter!=null)
        {
            param.addFilter(deptfilter);
        }
        if(feetypefilter !=null)
        {
            param.addFilter(feetypefilter);
        }
        jsonStr = JSON.toJSONString(param);
        System.out.println(jsonStr);
        json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/avgExpense/inpAvgExpense", jsonStr, "utf-8");
        //结果解析
        rs = JSONArray.parseObject(json, Map.class);
        List<Map<String, String>> qoqDataList = JSONArray.parseObject(rs.get("data").toString(), List.class);

        ////处理同比环比
        double value = Double.valueOf(dataList.get(0).get("value"));
        double yoyValue = Double.valueOf(yoyDataList.get(0).get("value"));
        double qoqValue = Double.valueOf(qoqDataList.get(0).get("value"));

        Map<String, String> result = new HashMap<String, String>();
        result.put("cost",String.valueOf(value));
        result.put("yoy", yoyValue == 0 ? "0" : (value * 100 / yoyValue <120 && value * 100 / yoyValue >80 ? value * 100 / yoyValue+ "" :(value * 100 / yoyValue)/1000 + 80+""));
        result.put("qoq", qoqValue == 0 ? "0" : (value * 100 / qoqValue <120 && value * 100 / qoqValue >80 ? value * 100 / qoqValue+ "" :(value * 100 / qoqValue)/1000 + 90+""));
        //同比
        if(yoyValue != 0 && value-yoyValue>0.0){
            result.put("yoy_flag","up");
        }else if(yoyValue != 0 && value-yoyValue==0.0){
            result.put("yoy_flag","down");
        }else if(yoyValue!=0 && value-yoyValue<0.0) result.put("yoy_flag","notchange");
        //环比
        if(qoqValue != 0 && value-qoqValue>0.0){
            result.put("qoq_flag","up");
        }else if(qoqValue != 0 && value-qoqValue==0.0){
            result.put("qoq_flag","notchange");
        }else if(qoqValue!=0 && value-qoqValue <0.0) {
            result.put("qoq_flag","down");
        }else{}
        String resultStr=JSON.toJSONString(Result.ok(result));
        logger.info("住院均次费用同比环比报表/rest/costControlEx/avgFeeYoYQoQ 结果:\n"+resultStr);
        return resultStr;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("inpRec")
    @ResponseBody
    @POST
    /**
     * 住院明细
     * 维度：时间、医院、科室、姓名
     */
    public String getInpRec(@RequestBody String params) throws UnsupportedEncodingException {
        //获取解析参数
        String originParam = URLDecoder.decode(params, "utf-8").substring(0, URLDecoder.decode(params, "utf-8").length() - 1);
        logger.info("住院明细报表/rest/costControlEx/inpRec 参数:\n"+originParam);
        Map<String, String> map = JSONArray.parseObject(originParam, Map.class);
        String year = map.get("year");
        String month = map.get("month");
        String deptname = map.get("department");
        String inpno = map.get("inhospitalnum");
        String patientname = map.get("name");
        String startDate = "";
        String endDate = "";
        if (year != null && !"".equals(year)) {
            if (month == null || "".equals(month)) {
                startDate = year + "-01-01";
                endDate = DateUtils.dateToString(DateUtils.getNextYearDate(DateUtils.stringToDate(year, "yyyy")), "yyyy-MM-dd");
            } else {
                startDate = year + "-" + month + "-01";
                endDate = DateUtils.dateToString(DateUtils.getNextMonthDate(DateUtils.stringToDate(year + month, "yyyyMM")), "yyyy-MM-dd");
            }
        }
        //构建指标接口参数对象
        ESParam param = new ESParam();
        if(! "".equals(startDate)) {
            ESQueryFilter filter = new ESQueryFilter("inp_date", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);
            param.addFilter(filter);
        }
        if(deptname!=null && ! "".equals(deptname)){
            ESQueryFilter filter = new ESQueryFilter("inp_dept_name", ESQueryFilter.FilterType.MATCH, deptname);
            param.addFilter(filter);
        }
        if(inpno!=null && ! "".equals(inpno)){
            ESQueryFilter filter = new ESQueryFilter("inp_no", ESQueryFilter.FilterType.MATCH, inpno);
            param.addFilter(filter);
        }
        if(patientname!=null && ! "".equals(patientname)){
            ESQueryFilter filter = new ESQueryFilter("patient_name", ESQueryFilter.FilterType.MATCH, patientname);
            param.addFilter(filter);
        }

        String jsonStr = JSON.toJSONString(param);
        System.out.println(jsonStr);
        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/quality/inpRec", jsonStr, "utf-8");
        //结果解析
        Map rs = JSONArray.parseObject(json, Map.class);
        List<Object> list = JSONArray.parseObject(rs.get("data").toString(), List.class);
        List<String[]> result=list.
                stream().
                map(x->JSON.parseObject(x.toString(),InpRecInfo.class).toStringArray()).
                collect(Collectors.toList());
        String resultStr=JSON.toJSONString(Result.ok(result));
        logger.info("住院明细报表/rest/costControlEx/inpRec 结果:\n"+resultStr);
        return resultStr;
    }
}