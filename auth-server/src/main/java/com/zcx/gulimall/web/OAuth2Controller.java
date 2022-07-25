package com.zcx.gulimall.web;


import com.alibaba.fastjson.JSON;
import com.zcx.common.utils.ExceptionCode;
import com.zcx.common.utils.HttpUtils;
import com.zcx.common.utils.R;
import com.zcx.gulimall.feign.MemberFeignService;

import com.zcx.gulimall.utils.GithubUtils;
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

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/oauth2.0")
public class OAuth2Controller
{


	@Autowired
	MemberFeignService memberFeignService;
	@Autowired
	GithubUtils githubUtils;

	@GetMapping("/github/success")
	public String github(@RequestParam String code, HttpSession session)
	{

		//第三方请求登录成功

		try {
			HttpResponse tres = githubUtils.getToken(code);
			if (tres.getStatusLine().getStatusCode()==200)
			{
				String json = EntityUtils.toString(tres.getEntity());
				GithubToken token = JSON.parseObject(json, GithubToken.class);
				String access_token = token.getAccess_token();


				HttpResponse ures = githubUtils.getUser(access_token);
				if (ures.getStatusLine().getStatusCode()==200)
				{
					String ujson = EntityUtils.toString(ures.getEntity());
					GithubUser user = JSON.parseObject(ujson, GithubUser.class);


					try {

						//登录成功
						R login = memberFeignService.login(user);
						session.setAttribute("loginUser",login.get("data"));
						return "redirect:http://gulimall.com";
					} catch (Exception e) {
						return "redirect:http://auth.gulimall.com/login.html";
					}
				}
			}
		} catch (Exception e) {

			//超时

		}

		//登录失败
		return "redirect:http://auth.gulimall.com/login.html";

	}




}
