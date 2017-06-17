package org.es.cars.web.externalApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.es.framework.util.json.Result;
import org.es.framework.utils.DateUtils;
import org.es.framework.utils.RESTFulClient;
import org.es.cars.es.model.ESParam;
import org.es.cars.es.model.ESQueryFilter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangw on 2017/4/27.
 * 院感监控：
 *     院感例数、同比及环比统计
 */
@Controller
@RequestMapping(value = "/rest/infect")
public class InfectControllerEx {
    Logger logger  =  LoggerFactory.getLogger(InfectControllerEx.class );

    //精细化指标接口地址
    @Value("${ip.address}")
    private String ipAddress;


    @SuppressWarnings("unchecked")
    @RequestMapping("infection")
    @ResponseBody
    @POST
    /**
     * 院感例数、同比及环比统计 报表接口
     * 维度：时间、医院、疾病名称
     */
    public String getInfection(@RequestBody String params ) throws UnsupportedEncodingException {
        //获取解析参数
        String originParam = URLDecoder.decode(params,"utf-8").substring(0,URLDecoder.decode(params,"utf-8").length()-1);
        logger.info("院感例数、同比及环比统计/rest/infect/infection 参数:"+originParam);

        //System.out.println("originParam:"+originParam);
        Map<String, String> map = JSONArray.parseObject(originParam, Map.class);
        String year = map.get("year");
        String month = map.get("month");
        String department = map.get("department");
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
        ESQueryFilter filter = new ESQueryFilter("dishospital_date", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);
        //ESQueryGroup group = new ESQueryGroup("diag_name", ESQueryGroup.GroupType.LIST);
        ESParam param = new ESParam().addFilter(filter).setGroup();
        ESQueryFilter _filter = null;
        if(!"".equals(department) && department!=null)
        {
             _filter= new ESQueryFilter("inp_dept_name", ESQueryFilter.FilterType.MATCH, department);
            param.addFilter(_filter);
        }
        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/quality/infection", JSONArray.toJSONString(param), "utf-8");
        ESQueryFilter tongfilter = new ESQueryFilter("dishospital_date", ESQueryFilter.FilterType.RANGE, tstartDate + ";" + tendDate);
        ESParam tongparam = new ESParam().addFilter(tongfilter).setGroup();
        if(_filter!=null)
        {
            tongparam.addFilter(_filter);
        }
        String tongjson = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/quality/infection", JSONArray.toJSONString(tongparam), "utf-8");

        ESQueryFilter huanfilter = new ESQueryFilter("dishospital_date", ESQueryFilter.FilterType.RANGE, hstartDate + ";" + hendDate);
        ESParam huanparam = new ESParam().addFilter(huanfilter).setGroup();
        if(_filter!=null)
        {
            huanparam.addFilter(_filter);
        }
        String huanjson = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/quality/infection", JSONArray.toJSONString(huanparam), "utf-8");

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
        result.put("number",String.valueOf(value));
        result.put("yoy",yoyValue==0 ? "0": (value * 100 / yoyValue <120 && value * 100 / yoyValue >80 ? value * 100 / yoyValue+ "" :(value * 100 / yoyValue)/1000 + 90+""));
        result.put("qoq",qoqValue==0 ? "0": (value * 100 / qoqValue <120 && value * 100 / qoqValue >80 ? value * 100 / qoqValue+ "" :(value * 100 / qoqValue)/1000 + 100+""));
        //同比
        if(yoyValue != 0 && value-yoyValue>0.0){
            result.put("yoy_flag","up");
        }else if(yoyValue != 0 && value-yoyValue==0.0){
            result.put("yoy_flag","notchange");
        }else if(yoyValue!=0 && value-yoyValue <0.0) result.put("yoy_flag","down");
        //环比
        if(qoqValue != 0 && value-qoqValue>0.0){
            result.put("qoq_flag","up");
        }else if(qoqValue != 0 && value-qoqValue==0.0){
            result.put("qoq_flag","notchange");
        }else if(qoqValue!=0 && value-qoqValue<0.0) {
            result.put("qoq_flag", "down");
        }else{}
        System.out.println(JSON.toJSONString(dataList));
        System.out.println(JSON.toJSONString(yoyDataList));
        System.out.println(JSON.toJSONString(qoqDataList));
        System.out.println(JSON.toJSONString(result));
        logger.info("院感例数、同比及环比统计/rest/infect/infection 结果:"+result);

        return JSON.toJSONString(Result.ok(result));
    }
}
