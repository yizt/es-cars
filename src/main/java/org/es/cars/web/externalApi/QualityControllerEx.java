package org.es.cars.web.externalApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.es.framework.util.json.Result;
import org.es.framework.utils.DateUtils;
import org.es.framework.utils.ParamsUtils;
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
 * Created by liuyoucai on 2017/4/27.
 * 质控检查报表
 */
@Controller
@RequestMapping(value = "/rest/qualityEx")
public class QualityControllerEx {
    Logger logger  =  LoggerFactory.getLogger(QualityControllerEx.class );
    @Value("${ip.address}")
    private String ipAddress;
    @SuppressWarnings("unchecked")
    @RequestMapping("treat")
    @ResponseBody
    @POST
    /**
     * 治疗结果
     * 维度：时间、医院、疾病名称
     * diag_datatime,org_id,diag_code,diag_name
     */
    public String getTreatResult(@RequestBody String params) throws UnsupportedEncodingException {
        //获取解析参数
        String originParam = URLDecoder.decode(params,"utf-8").substring(0,URLDecoder.decode(params,"utf-8").length()-1);
        logger.info("治疗结果报表接口/rest/qualityEx/treat 参数:"+originParam);
        Map<String, String> map = JSONArray.parseObject(originParam, Map.class);

        String year = map.get("year");
        String month = map.get("month");
        //获取科室
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
        ESParam param = new ESParam();
        if(!"".equals(startDate)) {
            ESQueryFilter filter = new ESQueryFilter("dishospital_date", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);
            param.addFilter(filter);
            param.setGroup();
        }
        if(department!=null && !"".equals(department))
        {
            ESQueryFilter filter = new ESQueryFilter("inp_dept_name", ESQueryFilter.FilterType.MATCH, department);
            param.addFilter(filter);
        }
        String jsonStr=JSON.toJSONString(param);
        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/quality/treat", jsonStr, "utf-8");
        String result= JSON.toJSON(Result.ok(ParamsUtils.pareseToHistogramEx(json,"diag_datatime"))).toString();
        logger.info("治疗结果报表接口/rest/qualityEx/treat 结果:"+result);
        return result;
    }
    @SuppressWarnings("unchecked")
    @RequestMapping("inpDeath")
    @ResponseBody
    @POST
    /**
     * 出院死亡人数
     * 维度：时间、医院、科室
     * org_id,diag_code,dept_name
     */
    public String getInpDeath(@RequestBody String params) throws UnsupportedEncodingException {
        //获取解析参数
        String originParam = URLDecoder.decode(params,"utf-8").substring(0,URLDecoder.decode(params,"utf-8").length()-1);
        logger.info("治疗结果报表接口/rest/qualityEx/treat 参数:"+originParam);
        Map<String, String> map = JSONArray.parseObject(originParam, Map.class);

        String year = map.get("year");
        String month = map.get("month");
        //获取科室
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
        ESParam param = new ESParam();
        if(!"".equals(startDate)) {
            ESQueryFilter filter = new ESQueryFilter("dishospital_date", ESQueryFilter.FilterType.RANGE, startDate + ";" + endDate);
            param.addFilter(filter);
            param.setGroup();
        }
        if(department!=null && !"".equals(department))
        {
            ESQueryFilter filter = new ESQueryFilter("inp_dept_name", ESQueryFilter.FilterType.MATCH, department);
            param.addFilter(filter);
        }
        String jsonStr=JSON.toJSONString(param);
        String json = RESTFulClient.restfulClient(ipAddress+"/es-med/rest/quality/treat", jsonStr, "utf-8");
        //返回结果
        Map rs = JSONArray.parseObject(json, Map.class);
        Map<String,Object> _result = new HashMap<String,Object>();
        List<Map<Object,Object>> dataList = JSONArray.parseObject(rs.get("data").toString(), List.class);
        for(Map<Object,Object> _map:dataList)
        {
            String ts = _map.get("treat_situation").toString();
            if("死亡".equals(ts))
            {
                _result.put("result",_map.get("value"));
            }
        }
        String result = JSON.toJSON(Result.ok(_result)).toString();
        logger.info("治疗结果报表接口/rest/qualityEx/treat 结果:"+result);
        return result;
    }
}
