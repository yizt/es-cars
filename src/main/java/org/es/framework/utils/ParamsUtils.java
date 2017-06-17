package org.es.framework.utils;

import com.alibaba.fastjson.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by liuyoucai on 2017/4/26.
 *
 */
public class ParamsUtils {
    //将前端传过来的参数转换成内部接口需要的参数格式
    public static Map<String,List<Map<Object,Object>>> parseArgs(String args,String dateType)
    {
        //解码
        String finaArgs="";
        try {
            finaArgs = URLDecoder.decode(args,"utf-8").substring(0,URLDecoder.decode(args,"utf-8").length()-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //将json字符串，转成map
        Map<Object,Object> argsMap = JSONArray.parseObject(finaArgs, Map.class);
        List<Map<Object,Object>> list1=null;
        List<Map<Object,Object>> list2=null;
        Map<String,List<Map<Object,Object>>> _msg =null;
        if(argsMap.get("year")!=null&&argsMap.size()==1)
        {
            list1 = new ArrayList<Map<Object,Object>>();
            list2 = new ArrayList<Map<Object,Object>>();
            _msg = new HashMap<String,List<Map<Object,Object>>>();
            String year = argsMap.get("year").toString();
            Date sdate = DateUtils.stringToDate(year + "-01-01");
            String edate = DateUtils.dateToString(DateUtils.getNextYearDate(sdate), "yyyy-MM-dd");
            String match_vale = year+"-01-01;"+edate;
            //过滤条件
            Map<Object,Object> _f = new HashMap<Object, Object>();
            _f.put("field",dateType);
            _f.put("filter_type","range");
            _f.put("match_value",match_vale);
            list1.add(_f);
            _msg.put("filter",list1);

            //分组条件
            Map<Object,Object> _g  = new HashMap<Object, Object>();
            _g.put("field",dateType);
            _g.put("group_type","date_histogram");
            _g.put("interval","month");
            _g.put("format","MM");
            list2.add(_g);
            _msg.put("group",list2);
            return _msg;
        }else if(argsMap.size()==2) {
            list1 = new ArrayList<Map<Object,Object>>();
            list2 = new ArrayList<Map<Object,Object>>();
            _msg = new HashMap<String,List<Map<Object,Object>>>();
                String year = argsMap.get("year").toString();
                String month = argsMap.get("month").toString();
                Date sdate = DateUtils.stringToDate(year+"-"+month+"-01");
                String edate = DateUtils.dateToString(DateUtils.getNextMonthDate(sdate), "yyyy-MM-dd");
                String match_vale = year+"-"+month+"-01;"+edate;
                //过滤条件
                Map<Object,Object> _f = new HashMap<Object, Object>();
                _f.put("field",dateType);
                _f.put("filter_type","range");
                _f.put("match_value",match_vale);
                list1.add(_f);
                _msg.put("filter",list1);

                //分组条件
                Map<Object,Object> _g  = new HashMap<Object, Object>();
                _g.put("field",dateType);
                _g.put("group_type","date_histogram");
                _g.put("interval","day");
                _g.put("format","dd");
                list2.add(_g);
                _msg.put("group",list2);
                return _msg;
            }else if(argsMap.size()==3){
                list1 = new ArrayList<Map<Object,Object>>();
                list2 = new ArrayList<Map<Object,Object>>();
                _msg = new HashMap<String,List<Map<Object,Object>>>();
                String year = argsMap.get("year").toString();
                String month = argsMap.get("month").toString();
                String day = argsMap.get("day").toString();
                Date sdate = DateUtils.stringToDate(year+"-"+month+"-"+day);
                String edate = DateUtils.dateToString(DateUtils.getNextDayDate(sdate), "yyyy-MM-dd");
                String match_vale = year+"-"+month+"-"+day+";"+edate;
                //过滤条件
                Map<Object,Object> _f = new HashMap<Object, Object>();
                _f.put("field",dateType);
                _f.put("filter_type","range");
                _f.put("match_value",match_vale);
                list1.add(_f);
                _msg.put("filter",list1);

                //分组条件
                Map<Object,Object> _g  = new HashMap<Object, Object>();
                _g.put("field",dateType);
                _g.put("group_type","date_histogram");
                _g.put("interval","day");
                _g.put("format","dd");
                list2.add(_g);
                _msg.put("group",list2);
                return _msg;
            }
        return null;

    }

    /**
     * 解析成柱状图
     * @param json
     * @return
     */
    public static Map<String,List<Object>> pareseToHistogram(String json,String dateType){
        Map rs = JSONArray.parseObject(json, Map.class);
        Map<String,List<Object>> _m = new HashMap<String,List<Object>>();
        List<Object> list1 = new ArrayList<Object>();
        List<Object> list2 = new ArrayList<Object>();
        List<Map<Object,Object>> dataList = JSONArray.parseObject(rs.get("data").toString(), List.class);
        for(Map<Object,Object> map:dataList)
        {
            Object val1 = map.get(dateType);
            Object val2 = map.get("value");
            list1.add(val1);
            list2.add(val2);
        }
        _m.put("xaxis",list1);
        _m.put("yaxis",list2);
        return _m;
    }

    /**
     * 解析成多层结构的柱状图
     * @param json
     * @return
     */
    public static Map<String,List<Object>> pareseToHistogramEx(String json,String dateType){
        Map rs = JSONArray.parseObject(json, Map.class);
        Map<String,List<Object>> _m = new HashMap<String,List<Object>>();
        List<Object> list1 = new ArrayList<Object>();
        List<Object> list2 = new ArrayList<Object>();
        List<Map<Object,Object>> dataList = JSONArray.parseObject(rs.get("data").toString(), List.class);
        for(Map<Object,Object> map:dataList)
        {
            Object val1 = map.get("treat_situation");
            Object val2 = map.get("value");
            Map<Object,Object> m1 = new HashMap<Object, Object>();
            m1.put("name",val1);
            m1.put("value",val2);
            list1.add(val1);
            list2.add(m1);
        }
        _m.put("legenddata",list1);
        _m.put("seriesdata",list2);
        return _m;
    }
}
