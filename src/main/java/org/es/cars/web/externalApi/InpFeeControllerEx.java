package org.es.cars.web.externalApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.es.framework.util.json.Result;
import org.es.framework.utils.DateUtils;
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
import java.util.*;
/**
 * Created by zhangw on 2017/4/27.
 * 住院费用统计：
 *    上月住院均次费用
 *    上月住院均次费用各科室排名Top20
 *    上月住院均次费用构成
 */
@Controller
@RequestMapping(value = "/rest/inpFee")
public class InpFeeControllerEx {
    Logger logger  =  LoggerFactory.getLogger(InpFeeControllerEx.class );

    //精细化指标接口地址
    @Value("${ip.address}")
    private String ipAddress;

    @SuppressWarnings("unchecked")
    @RequestMapping("inpAvgFee")
    @ResponseBody
    @POST
    /**
     * 上月住院均次费用、同比、环比
     *
     */
    public String getInpAvgFee(@RequestBody String params ) throws UnsupportedEncodingException {
        //获取解析参数
        String originParam = URLDecoder.decode(params,"utf-8").substring(0,URLDecoder.decode(params,"utf-8").length()-1);
        logger.info("上月住院均次费用、同比、环比/rest/inpFee/inpAvgFee 参数:"+originParam);

        //System.out.println("originParam:"+originParam);
        Map<String, String> map = JSONArray.parseObject(originParam, Map.class);
        String year = map.get("year");
        String month = map.get("month");
        String startDate = "";
        String endDate = "";
        String tstartDate="";//同比开始时间
        String tendDate = "";//同比结束时间
        String hstartDate="";//环比
        String hendDate = "";//环比
        //上月时间，比如2017-03-01 2017-04-01
        //同比，对应的2016-03-01  2016-04-01
        //环比，对应的2017-02-01  2017-03-01
        if (month == null || "".equals(month)) {
            startDate = year + "-01-01";
            endDate = DateUtils.dateToString(DateUtils.getNextYearDate(DateUtils.stringToDate(year, "yyyy")), "yyyy-MM-dd");
            tstartDate=DateUtils.dateToString(DateUtils.getLastYearDate(DateUtils.stringToDate(year , "yyyy")), "yyyy-MM-dd");
            tendDate=DateUtils.dateToString(DateUtils.getNextMonthDate(DateUtils.stringToDate(tstartDate, "yyyy-MM-dd")), "yyyy-MM-dd");
            hstartDate=DateUtils.dateToString(DateUtils.getLastMonthDate(DateUtils.stringToDate(year, "yyyy")), "yyyy-MM-dd");
            hendDate=startDate;
        } else {
            startDate = year + "-" + month + "-01";
            endDate = DateUtils.dateToString(DateUtils.getNextMonthDate(DateUtils.stringToDate(year + month, "yyyyMM")), "yyyy-MM-dd");
            tstartDate=DateUtils.dateToString(DateUtils.getLastYearDate(DateUtils.stringToDate(year+ month, "yyyyMM")), "yyyy-MM-dd");
            tendDate=DateUtils.dateToString(DateUtils.getNextMonthDate(DateUtils.stringToDate(tstartDate, "yyyy-MM-dd")), "yyyy-MM-dd");
            hstartDate=DateUtils.dateToString(DateUtils.getLastMonthDate(DateUtils.stringToDate(year + month, "yyyyMM")), "yyyy-MM-dd");
            hendDate=startDate;
        }
        System.out.println("startDate:"+startDate+",endDate:"+endDate+";tstartDate:"+tstartDate
        +",tendDate:"+tendDate+";hstartDate:"+hstartDate+";hendDate:"+hendDate);
        //构建指标接口参数对象
        ESQueryFilter filter = new ESQueryFilter("cost_date", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);
        //ESQueryGroup group = new ESQueryGroup("dept_name", ESQueryGroup.GroupType.LIST);

        ESParam param = new ESParam().setFilter(filter).setGroup();

        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/avgExpense/inpAvgExpense", JSONArray.toJSONString(param), "utf-8");
        System.out.println("json:"+json);

        ESQueryFilter tongfilter = new ESQueryFilter("cost_date", ESQueryFilter.FilterType.RANGE, tstartDate + ";" + tendDate);
        ESParam tongparam = new ESParam().setFilter(tongfilter).setGroup();
        String tongjson = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/avgExpense/inpAvgExpense", JSONArray.toJSONString(tongparam), "utf-8");
        System.out.println("tongjson:"+tongjson);

        ESQueryFilter huanfilter = new ESQueryFilter("cost_date", ESQueryFilter.FilterType.RANGE, hstartDate + ";" + hendDate);
        ESParam huanparam = new ESParam().setFilter(huanfilter).setGroup();
        String huanjson = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/avgExpense/inpAvgExpense", JSONArray.toJSONString(huanparam), "utf-8");
        System.out.println("huanjson:"+huanjson);
        Map rs = JSONArray.parseObject(json, Map.class);
        List<Map<String, String>> dataList = JSONArray.parseObject(rs.get("data").toString(), List.class);
        rs = JSONArray.parseObject(tongjson, Map.class);
        List<Map<String, String>> yoyDataList = JSONArray.parseObject(rs.get("data").toString(), List.class);
        rs = JSONArray.parseObject(huanjson, Map.class);
        List<Map<String, String>> qoqDataList = JSONArray.parseObject(rs.get("data").toString(), List.class);
        ////处理同比环比
        double value=Double.valueOf(dataList.get(0).get("value"));
        double yoyValue=Double.valueOf(yoyDataList.get(0).get("value"));
        double qoqValue=Double.valueOf(qoqDataList.get(0).get("value"));

        Map<String,String> result=new HashMap<String,String>();
        result.put("tongqi",yoyValue==0 ? "--%": value * 100/yoyValue +"%");
        result.put("tongbi",yoyValue==0 ? "--%": value * 100/qoqValue +"%");
        System.out.println(JSON.toJSONString(dataList));
        System.out.println(JSON.toJSONString(yoyDataList));
        System.out.println(JSON.toJSONString(qoqDataList));
        System.out.println(JSON.toJSONString(result));
        logger.info("上月住院均次费用、同比、环比/rest/inpFee/inpAvgFee 结果:"+result);

        return JSON.toJSONString(Result.ok(result));
    }
    @SuppressWarnings("unchecked")
    @RequestMapping("inpAvgDeptRank")
    @ResponseBody
    @POST
    /**
     * 上月住院均次费用各科室排名Top20
     *
     * 住院均次费用
     * 住院人次
     * 住院总费用
     */
    public String getInpAvgDeptRank(@RequestBody String params ) throws Exception {
        System.out.println("params:"+params);
        //获取解析参数
        String originParam = URLDecoder.decode(params,"utf-8").substring(0,URLDecoder.decode(params,"utf-8").length()-1);
        logger.info("上月住院均次费用各科室排名Top10/rest/inpFee/inpAvgDeptRank 参数:"+originParam);

        Map<String, String> map = JSONArray.parseObject(originParam, Map.class);
        String year = map.get("year");
        String month = map.get("month");
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
        ESQueryGroup group = new ESQueryGroup("dept_name", ESQueryGroup.GroupType.LIST);

        ESParam param = new ESParam().setFilter(filter).setGroup(group);
        //String str=JSONArray.toJSONString(param);
        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/avgExpense/inpAvgExpense", JSONArray.toJSONString(param), "utf-8");
        System.out.println("json:"+json);
        Map rs = JSONArray.parseObject(json, Map.class);
        Map<String,Object> _m = new HashMap<String,Object>();
        List<Map<String,String>> dataList = JSONArray.parseObject(rs.get("data").toString(), List.class);

        //排序取top10
        dataList.sort(new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                return new Double((Double.valueOf(o2.get("value")) - Double.valueOf(o1.get("value"))) * 100000).intValue();
            }
        });
        List<Map<String, String>> newList = new ArrayList<Map<String, String>>();
        if(dataList.size()>10){

        }
        for (int i = 0; i <= 10 && i < dataList.size(); i++) {
            newList.add(dataList.get(i));
        }
        String[][] resultData = newList.stream().
                map(elem -> new String[]{elem.get("dept_name").toString(), elem.get("value").toString()}).
                toArray(String[][]::new);
        _m.put("average",resultData);

        //总费用
        String sumjson = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/medicalIn/dischargeIncome", JSONArray.toJSONString(param), "utf-8");
        System.out.println("sumjson:"+sumjson);
        Map sumrs = JSONArray.parseObject(sumjson, Map.class);
        List<Map<String,String>> sumdataList = JSONArray.parseObject(sumrs.get("data").toString(), List.class);
        //排序取top10
        sumdataList.sort(new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                return new Double((Double.valueOf(o2.get("value")) - Double.valueOf(o1.get("value"))) * 100000).intValue();
            }
        });
        List<Map<String, String>> newsumdataList = new ArrayList<Map<String, String>>();
        for (int i = 0; i <= 10 && i < sumdataList.size(); i++) {
            newsumdataList.add(sumdataList.get(i));
        }
        String[][] sumresultData = newsumdataList.stream().
                map(elem -> new String[]{elem.get("dept_name").toString(), elem.get("value").toString()}).
                toArray(String[][]::new);
        _m.put("sumcost",sumresultData);

        //人次
        filter = new ESQueryFilter("inp_date", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);
        group = new ESQueryGroup("inp_dept_name", ESQueryGroup.GroupType.LIST);
        param = new ESParam().setFilter(filter).setGroup(group);
        String rcjson = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/portfolio/inptime", JSONArray.toJSONString(param), "utf-8");
        System.out.println("rcjson:"+rcjson);
        Map rcrs = JSONArray.parseObject(rcjson, Map.class);
        List<Map<String,String>> rcdataList = JSONArray.parseObject(rcrs.get("data").toString(), List.class);
        //排序取top10
        rcdataList.sort(new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                return new Double((Double.valueOf(o2.get("value")) - Double.valueOf(o1.get("value"))) * 100000).intValue();
            }
        });
        List<Map<String, String>> newrcdataList = new ArrayList<Map<String, String>>();
        for (int i = 0; i <= 10 && i < rcdataList.size(); i++) {
            newrcdataList.add(rcdataList.get(i));
        }
        String[][] manresultData = newrcdataList.stream().
                map(elem -> new String[]{elem.get("inp_dept_name").toString(), elem.get("value").toString()}).
                toArray(String[][]::new);
        _m.put("mantimes",manresultData);

        //System.out.println("====="+JSON.toJSON(Result.ok(_m)).toString());
        String result=JSON.toJSON(Result.ok(_m)).toString();
        logger.info("上月住院均次费用各科室排名Top10/rest/inpFee/inpAvgDeptRank 参数:"+result);

        return result;
    }



    @SuppressWarnings("unchecked")
    @RequestMapping("inpAvgPart")
    @ResponseBody
    @POST
    /**
     * 上月住院均次费用构成
     *  http://localhost:1180/es-med/rest/inpFee/inpAvgPart
     * {"data":{"legenddata":["检查","药品","检验"],
     * "seriesdata":[{"cost_type_name":"检查","value":"13.6"},
     * {"cost_type_name":"药品","value":"38.25806451612903"},
     * {"cost_type_name":"检验","value":"13.0"}]},
     * "message":"查询成功","status":true}
     */
    public String getInpAvgPart(@RequestBody String params ) throws UnsupportedEncodingException {
        //System.out.println("params:"+params);
        //获取解析参数
        String originParam = URLDecoder.decode(params,"utf-8").substring(0,URLDecoder.decode(params,"utf-8").length()-1);
        logger.info("上月住院均次费用构成/rest/inpFee/inpAvgPart 参数:"+originParam);

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
        ESQueryGroup group = new ESQueryGroup("cost_type_name", ESQueryGroup.GroupType.LIST);
        ESParam param = new ESParam().addFilter(filter).setGroup(group);
        if(department!=null && !"".equals(department))
        {
            ESQueryFilter deptfilter = new ESQueryFilter("dept_name", ESQueryFilter.FilterType.MATCH, department);
            param.addFilter(deptfilter);
        }
        //String str=JSONArray.toJSONString(param);
        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/avgExpense/inpAvgExpense", JSONArray.toJSONString(param), "utf-8");
        System.out.println("json:"+json);
        Map rs = JSONArray.parseObject(json, Map.class);
        Map<String,Object> _m = new HashMap<String,Object>();
        List<Object> list1 = new ArrayList<Object>();
        List<Object> list2 = new ArrayList<Object>();
        List<Map<Object,Object>> dataList = JSONArray.parseObject(rs.get("data").toString(), List.class);
        List<Map<Object,Object>> newList=new ArrayList<Map<Object,Object>>();
        for(Map<Object,Object> m:dataList)
        {
            Object val1 = m.get("cost_type_name");
            Object val2 = m.get("value");
            list1.add(val1);
            list2.add(val2);
            Map<Object,Object> map2=new HashMap<Object,Object>();
            map2.put("name",val1);
            map2.put("value",val2);
            newList.add(map2);
        }
        _m.put("legenddata",list1);
        _m.put("seriesdata",newList);
        String result=JSON.toJSON(Result.ok(_m)).toString();
        logger.info("上月住院均次费用构成/rest/inpFee/inpAvgPart 参数:"+result);

        return result;
    }

}
