package com.zcx.gulimall.utils;

import com.zcx.common.utils.HttpUtils;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@ConfigurationProperties(prefix = "gulimall.oauth.gitee")
@Component
@Data
public class GiteeUtils implements  OAuthUtils
{
	
	private String client_id;
	private String client_secret;
	
	@Override
	public  HttpResponse getToken(String code) throws Exception
	{

		Map<String,String> bodys=new HashMap<>();
		bodys.put("grant_type","authorization_code");
		bodys.put("code",code);
		bodys.put("client_secret",client_secret);
		bodys.put("client_id",client_id);
		bodys.put("redirect_uri","http://auth.gulimall.com/oauth2.0/gitee/success");
		Map<String,String>header=new HashMap<>();
		
		return HttpUtils.doPost("https://gitee.com", "/oauth/token", "post",
				header, null, bodys);
	}
	
	
	@Override
	public HttpResponse getUser(String access_token) throws Exception
	{
		Map<String,String> querys=new HashMap<>();
		querys.put("access_token",access_token);
		
	
		return HttpUtils.doGet("https://gitee.com",
				"/api/v5/user", "get",
				new HashMap<>(), querys);
		
	}
}
