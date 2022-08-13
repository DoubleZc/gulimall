package com.zcx.gulimall.order.listener;


import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.zcx.gulimall.order.vo.AlipayParam;
import com.zcx.gulimall.order.config.AlipayTemplate;
import com.zcx.gulimall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
public class AlipayListener
{
	@Autowired
	OrderService orderService;
	
	@Autowired
	AlipayTemplate alipayTemplate;
	
	@PostMapping("/feign/payed/notify")
	public String successAlipay(AlipayParam param,HttpServletRequest request) throws UnsupportedEncodingException, AlipayApiException
	{
		
		Map<String,String> params = new HashMap<>();
		Map<String,String[]> requestParams = request.getParameterMap();
		for (String name : requestParams.keySet()) {
			String[] values = requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用
		//	valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(), alipayTemplate.getCharset(), alipayTemplate.getSign_type()); //调用SDK验证签名
		log.info("signVerified"+signVerified);
		
		
		if(signVerified)
		{
			
			return orderService.handlePayResult(param);
			
		}else
		{
			return "error";
		}
		
		
		
	}
	
}
