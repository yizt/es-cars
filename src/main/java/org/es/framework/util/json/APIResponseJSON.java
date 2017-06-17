package org.es.framework.util.json;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class APIResponseJSON {

	public static final String CONTENT_TYPE_CONTENT = "application/json;charset=UTF-8";

	public static final String CONTENT_TYPE = "Content-Type";

	public  Map<String, Object> result = new HashMap<String, Object>();
	
	private Object data; 
	
	private String[] filterNameForData = null;
	
	private APIResponseJSON() {
	}
	
	public Object getData() {
		return data;
	}

	public APIResponseJSON addDatePair(String key,Object value){
		if(data == null){
			data = new HashMap<String,Object>();
		}else if(!(data instanceof Map)){
			throw new RuntimeException("addDatePair must be hashmap");
		}
		((Map<String,Object>)data).put(key,value);
		return this;
	}

	public APIResponseJSON setData(Object data) {
		this.data = data;
		return this;
	}
	
	
	public static APIResponseJSON instance() {
		return new APIResponseJSON();
	}
	
	
	public String toJSON() {
		return JsonUtils.toJson(this, this.filterNameForData);
	}

	public ResponseEntity<String> responseEntity() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(CONTENT_TYPE, CONTENT_TYPE_CONTENT);
		return new ResponseEntity<String>(this.toJSON(), responseHeaders, HttpStatus.OK);
	}
	
	public APIResponseJSON setMsg(String msg){
		this.result.put("msg", msg);
		return this;
	}
	
	public APIResponseJSON setCode(int code){
		this.result.put("code", code);
		return this;
	}


	
	public APIResponseJSON addFilterName(String... names) {
		this.filterNameForData = names;
		return this;
	}
	
}
