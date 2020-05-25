package com.web_back.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.web_back.annotation.PassToken;
import com.web_back.entity.User;
import com.web_back.service.UserService;
import com.web_back.utils.TokenException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        String token = httpServletRequest.getHeader("Authorization");// 从 http 请求头中取出 token
        // 如果不是映射到方法直接通过
        if(!(object instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod handlerMethod=(HandlerMethod)object;
        Method method=handlerMethod.getMethod();
        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                return true;
            }
        }
        // 执行认证
        if (token == null) {
        	throw new TokenException("无token，请重新登录");
        }
        // 获取 token 中的 user id
        Integer userId=null;
        try {
        	userId = Integer.valueOf( JWT.decode(token).getAudience().get(0));
        } catch (JWTDecodeException j) {
        	throw new TokenException("token解析出错，请错新登录");
        }
        User userParams=new User();
        userParams.setId(userId);
        User user = userService.getUser(userParams,false).get(0);
        if (user == null) {
        	throw new TokenException("用户不存在，请重新登录");
        }
        httpServletRequest.setAttribute("currentUser", user);
        // 验证 token
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getRole()+user.getPassword())).build();
        try {
        	jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
        	throw new TokenException("token验证出错，请重新登录");
        }
        return true;
    }

    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
