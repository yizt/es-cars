package org.es.framework.util.json;
/**
 * 自定义响应结构
 */
public class Header {
    // 响应业务状态
    private String message;

    // 响应消息
    private boolean status;

    
	public Header(String message, boolean status) {
		super();
		this.message = message;
		this.status = status;
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
}
