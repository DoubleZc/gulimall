package com.zcx.gulimall.seckill.config;


import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Configuration
@Slf4j
public class SentinelConfig implements BlockExceptionHandler
{
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception
	{
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		PrintWriter writer = response.getWriter();
		writer.write("访问频繁");
		writer.flush();
		writer.close();
		
		
	}
}
