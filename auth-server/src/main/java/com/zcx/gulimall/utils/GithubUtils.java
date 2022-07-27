package com.zcx.gulimall.utils;

import com.zcx.common.utils.HttpUtils;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;



@ConfigurationProperties(prefix = "gulimall.oauth.github")
@Component
@Data
public class GithubUtils implements OAuthUtils
{
	private String client_id;
	private String client_secret;



	@Override
	public  HttpResponse getToken(String code) throws Exception
	{
		Map<String,String> heads=new HashMap<>();
		heads.put("Accept","application/json");
		Map<String,String> bodys=new HashMap<>();

		bodys.put("code",code);
		bodys.put("client_secret",client_secret);
		bodys.put("client_id",client_id);

		return HttpUtils.doPost("https://github.com", "/login/oauth/access_token", "post",
				heads, null, bodys);
	}


	@Override
	public HttpResponse getUser(String access_token) throws Exception
	{
		Map<String, String> uheads = new HashMap<>();
		uheads.put("Authorization","token "+access_token);
		return HttpUtils.doGet("https://api.github.com",
				"/user", "get",
				uheads, null);

	}
}
