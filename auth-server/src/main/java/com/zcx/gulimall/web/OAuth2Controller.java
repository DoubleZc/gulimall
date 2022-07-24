package com.zcx.gulimall.web;


import com.alibaba.fastjson.JSON;
import com.zcx.common.utils.ExceptionCode;
import com.zcx.common.utils.HttpUtils;
import com.zcx.common.utils.R;
import com.zcx.gulimall.feign.MemberFeignService;
import com.zcx.gulimall.properties.OAuthProperties;
import com.zcx.gulimall.vo.github.GithubToken;
import com.zcx.gulimall.vo.github.GithubUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/oauth2.0")
public class OAuth2Controller
{
	@Autowired
	OAuthProperties properties;

	@Autowired
	MemberFeignService memberFeignService;

	@GetMapping("/github/success")
	public String github(@RequestParam String code) throws Exception
	{

		Map<String,String> heads=new HashMap<>();
		heads.put("Accept","application/json");
		Map<String,String> bodys=new HashMap<>();

		bodys.put("code",code);
		bodys.put("client_secret",properties.getClient_secret());
		bodys.put("client_id",properties.getClient_id());
		//第三方请求登录成功
		HttpResponse tres = HttpUtils.doPost("https://github.com", "/login/oauth/access_token", "post",
				heads, null, bodys);

		if (tres.getStatusLine().getStatusCode()==200)
		{
			String json = EntityUtils.toString(tres.getEntity());
			GithubToken token = JSON.parseObject(json, GithubToken.class);
			String access_token = token.getAccess_token();

			Map<String, String> uheads = new HashMap<>();
			uheads.put("Authorization","token "+access_token);
			HttpResponse ures = HttpUtils.doGet("https://api.github.com",
					"/user", "get",
					uheads, null);
			if (ures.getStatusLine().getStatusCode()==200)
			{
				String ujson = EntityUtils.toString(ures.getEntity());
				GithubUser user = JSON.parseObject(ujson, GithubUser.class);
				//登录成功

				try {
					R login = memberFeignService.login(user);
					return "redirect:http://gulimall.com";
				} catch (Exception e) {
					e.printStackTrace();
				}


			}
		}
		//登录失败
		System.out.println("登录失败");

		return null;
	}




}
