package org.es.framework.mvc.exception;

public class BussinessRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -7936621829410590188L;

	private int code = 0;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public BussinessRuntimeException() {
		super();
	}

	public BussinessRuntimeException(int code, String message) {
		super(message);
		this.code = code;
	}

	public BussinessRuntimeException(String message, Throwable cause) {
		super(message, cause);
		this.code = 1;
	}

	public BussinessRuntimeException(String message) {
		super(message);
		this.code = 1;
	}

	public BussinessRuntimeException(Throwable cause) {
		super(cause);
		this.code = 1;
	}

}
