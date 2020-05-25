package com.web_back.interceptor;

import com.web_back.utils.ActionResult;
import com.web_back.utils.BaseApi;
import com.web_back.utils.TokenException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GloablExceptionHandler extends BaseApi {
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ActionResult handleException(Exception e) {
        String msg = e.getMessage();
        if (msg == null || msg.trim().equals("")) {
            msg = "服务端出错！";
        }
        if(e instanceof TokenException) {
        	return noToken(msg);
        }else {
        	return error(msg);
        }
    }
}
