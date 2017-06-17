package org.es.framework.util.json;
/**
 * /**
 * Created by liuyoucai on 2017/4/22.
 * 自定义外部接口响应结构
 */
public class Result {
    // 响应业务状态
    private String message;

    // 响应消息
    private boolean status;

    //返回数据
    private Object data;

    public static Result ok(Object data) {
        return new Result("查询成功",true,data);
    }

    public static Result ok() {
        return new Result("查询成功",true,null);
    }

    public static Result fail(Object data) {
        return new Result("查询失败",false,data);
    }

    public static Result fail() {
        return new Result("查询失败",false,null);
    }


    public Result(String message, boolean status, Object data) {
        super();
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
