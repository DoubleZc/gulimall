package com.zcx.gulimall.controller;


import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.zcx.common.utils.ExceptionCode;
import com.zcx.common.utils.HttpUtils;
import com.zcx.common.utils.R;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;



@ConfigurationProperties(prefix = "gulimall.sms")
@Data
@RestController
@Slf4j
public class SmsController
{
	String smsSignId;
	String templateId;
	String appcode ;


	@GetMapping("/sms")
	public R sendSmsCode(@RequestParam("phone") String phone, @RequestParam("code") String code)
	{
		if (true)
		{
			log.info("==========================发送成功 code:{}===============================",code);
			return 	R.ok();
		}



		String host = "https://gyytz.market.alicloudapi.com";
		String path = "/sms/smsSend";
		String method = "POST";

		Map<String, String> headers = new HashMap<String, String>();
		//最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
		headers.put("Authorization", "APPCODE " + appcode);
		Map<String, String> querys = new HashMap<String, String>();
		querys.put("mobile", phone);
		querys.put("param", "**code**:"+code+",**minute**:5");
		querys.put("smsSignId",smsSignId );
		querys.put("templateId", templateId);
		Map<String, String> bodys = new HashMap<String, String>();
		try {
			/**
			 * 重要提示如下:
			 * HttpUtils请从
			 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
			 * 下载
			 *
			 * 相应的依赖请参照
			 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
			 */
			HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);;
			//获取response的body
			if(EntityUtils.toString(response.getEntity()).contains("成功")) {

				return R.ok();
			}
		} catch (Exception e) {
			return R.error(e.getMessage());
		}
		return R.error();

	}


}
