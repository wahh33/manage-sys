package com.web_back.utils;

import java.sql.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.web_back.entity.User;

public class TokenUtils {
	public static String getToken(User user) {
		String token="";
		token = JWT.create().withAudience(String.valueOf(user.getId()))// 将 user id 保存到 token 里面
				   .withExpiresAt(new Date(System.currentTimeMillis()+24*60*60*1000))//过期时间1天
				   .sign(Algorithm.HMAC256(user.getRole()+user.getPassword()));// 以 password 作为 token 的密钥
        return token;
	}
}