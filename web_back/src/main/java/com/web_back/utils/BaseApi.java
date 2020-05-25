package com.web_back.utils;

public class BaseApi {
	public static ActionResult success(String message,Object data) {
		return new ActionResult(ActionResult.Success,message,data);
	}
	public static ActionResult success(String message) {
		return new ActionResult(ActionResult.Success,message);
	}
	public static ActionResult error(String message,Object data) {
		return new ActionResult(ActionResult.Error,message,data);
	}
	public static ActionResult error(String message) {
		return new ActionResult(ActionResult.Error,message);
	}
	public static ActionResult noToken(String message,Object data) {
		return new ActionResult(ActionResult.NoToken,message,data);
	}
	public static ActionResult noToken(String message) {
		return new ActionResult(ActionResult.NoToken,message);
	}
}
