package org.es.framework.util.json;

/**
 * /**
 * Created by liuyoucai on 2017/4/22.
 * 自定义内部接口响应结构
 */
public class ResultSet {

    // 响应业务状态
    private Header header;

    // 响应中的数据
    private Object data;

    public static ResultSet build( Header header, Object data) {
        return new ResultSet(header, data);
    }

    public static ResultSet ok(Object data) {
        return new ResultSet("成功",true,data);
    }
    
    public static ResultSet ok() {
        return new ResultSet("成功",true,null);
    }
    
    public static ResultSet fail(Object data) {
   	 	return new ResultSet("无数据",false,data);
    }
    
    public static ResultSet fail() {
   	 	return new ResultSet("其他",false,null);
    }
    
    public ResultSet() {

    }
    
    public static ResultSet build(Integer status, Header header) {
        return new ResultSet(header, null);
    }
    
    public ResultSet(Header header, Object data) {
        this.header = header;
        this.data = data;
    }
    
    public ResultSet(String flag,boolean isSuc,Object data) {
        this.header = new Header(flag,isSuc);
        this.data = data;
    }
    
    public Header getHeader() {
		return header;
	}
    
	public void setHeader(Header header) {
		this.header = header;
	}
	
	public Object getData() {
        return data;
    }
	
    public void setData(Object data) {
        this.data = data;
    }
}
