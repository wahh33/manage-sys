package com.web_back.utils;

public class ActionResult {
	public static int Success=200;
	public static int Error=0;
	public static int NoToken=401;
	
	private int code;
	private String message;
	private Object data;
	
	public ActionResult(int code, String message, Object data) {
		super();
		this.code = code;
		this.message = message;
		this.data = data;
	}	
	
	public ActionResult(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}

