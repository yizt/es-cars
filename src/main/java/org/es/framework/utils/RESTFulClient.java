package org.es.framework.utils;


import com.alibaba.fastjson.JSONArray;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyoucai on 2017/4/22.
 * restful接口客户端
 */
public class RESTFulClient {
    public static String restfulClient(String targetRUL,Map<String,List<Map<Object,Object>>> map, String encode) {
        return restfulClient(targetRUL,JSONArray.toJSONString(map),encode);
    }

    /**
     * add by mick.yi 2017-4-27
     * @param targetRUL
     * @param body
     * @param encode
     * @return
     */
    public static String restfulClient(String targetRUL,String body, String encode) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(targetRUL);
        try {
            httppost.addHeader("Content-type", "application/json");
            httppost.setEntity(new StringEntity(body,encode));
            HttpResponse response = httpclient.execute(httppost);
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity());
            } else {
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
