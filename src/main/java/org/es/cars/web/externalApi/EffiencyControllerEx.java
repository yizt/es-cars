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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyoucai on 2017/4/28.
 * 工作效率报表接口
 */
@Controller
@RequestMapping(value = "/rest/effiencyEx")
public class EffiencyControllerEx {
    Logger logger  =  LoggerFactory.getLogger(EffiencyControllerEx.class );
    @Value("${ip.address}")
    private String ipAddress;

    @SuppressWarnings("unchecked")
    @RequestMapping("effiency")
    @ResponseBody
    @POST
    public String getEffiencyCount(@RequestBody String params) throws UnsupportedEncodingException {
        //获取解析参数
        String originParam = URLDecoder.decode(params,"utf-8").substring(0,URLDecoder.decode(params,"utf-8").length()-1);
        logger.info("工作效率报表接口/rest/effiencyEx/effiency 参数:"+originParam);
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
        ESQueryFilter filter = new ESQueryFilter("visit_datetime", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);
        ESParam param = new ESParam().setFilter(filter).setGroup();
        String jsonStr=JSON.toJSONString(param);
        //1.全院医师日均门诊人次
        String docAvgOutpCount = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/effiency/docAvgOutp", jsonStr, "utf-8");
        //解析结果
        Map rs = JSONArray.parseObject(docAvgOutpCount, Map.class);
        List<Map<String, String>> daocDataList = JSONArray.parseObject(rs.get("data").toString(), List.class);
        String value = daocDataList.get(0).get("value");
        Map<String,String> result = new HashMap<String,String>();
        result.put("全院医师日均门诊人次",value);
        //2.日均门诊人次
        String avgOutpCount = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/effiency/avgOutp", jsonStr, "utf-8");
        rs = JSONArray.parseObject(avgOutpCount, Map.class);
        List<Map<String, String>> aocDataList = JSONArray.parseObject(rs.get("data").toString(), List.class);
        value = aocDataList.get(0).get("value");
        result.put("日均门诊人次",value);
        //3.日均出诊医师数
        String docAvgCount = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/effiency/docAvg", jsonStr, "utf-8");
        rs = JSONArray.parseObject(docAvgCount, Map.class);
        List<Map<String, String>> dacDataList = JSONArray.parseObject(rs.get("data").toString(), List.class);
        value = dacDataList.get(0).get("value");
        result.put("日均出诊医师数",value);
        //4.出诊医师日均门诊人次
        String avgDocOutpCount = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/effiency/avgDocOutp", jsonStr, "utf-8");
        rs = JSONArray.parseObject(avgDocOutpCount, Map.class);
        List<Map<String, String>> adocDataList = JSONArray.parseObject(rs.get("data").toString(), List.class);
        value = adocDataList.get(0).get("value");
        result.put("日均出诊医师门诊人次",value);
        Map<String,List<Object>> _m = new HashMap<String,List<Object>>();
        List<Object> list1 = new ArrayList<Object>();
        List<Object> list2 = new ArrayList<Object>();
        //遍历map
        for(Map.Entry<String, String> entry : result.entrySet()){
            Object val1 = entry.getKey();
            Object val2 = entry.getValue();
            list1.add(val1);
            list2.add(val2);
        }
        _m.put("xaxis",list1);
        _m.put("yaxis",list2);
        String _result =JSON.toJSON(Result.ok(_m)).toString();
        logger.info("工作效率报表接口接口/rest/effiencyEx/effiency 结果:"+_result);
        return _result;
    }

}
