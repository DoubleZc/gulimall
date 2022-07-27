package com.zcx.common.utils;


import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class JWTUntils
{

	//秘钥
	static String  secure="zhangsan";


	public static String getToken(Object o)
	{

		System.out.println(secure);


		// 指定token过期时间
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 10);
		//设置header
		Map<String, Object> header = new HashMap<>();
		header.put("alg", "HS256");
		header.put("typ", "JWT");

		String json = JSON.toJSONString(o);
		JWTCreator.Builder builder = JWT.create();
		//设置payload
		builder.withClaim("data",json);
		return builder.withHeader(header)
				.withExpiresAt(calendar.getTime())  // 过期时间
				.sign(Algorithm.HMAC256(secure));
	}


	public static <T> T ResolveToken(String token, Class<T> tClass)
	{
		JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secure)).build();
		//验证sign  若不一致抛出SignatureVerificationException
		DecodedJWT decodedJWT = jwtVerifier.verify(token);
		
		
		
		Claim data = decodedJWT.getClaim("data");
		return JSON.parseObject(data.asString(), tClass);
		
	}


}
