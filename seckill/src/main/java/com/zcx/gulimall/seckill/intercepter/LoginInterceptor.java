package com.zcx.gulimall.seckill.intercepter;


import com.zcx.common.constant.AuthConstant;
import com.zcx.common.to.MemberTo;
import com.zcx.common.utils.MyThreadMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor
{
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		String requestURI = request.getRequestURI();
		log.info("访问：{}",request.getRequestURL());
		
		
		AntPathMatcher antPathMatcher = new AntPathMatcher();
		boolean match = antPathMatcher.match("/kill", requestURI);
		
		
		if (match){
			HttpSession session = request.getSession();
			MemberTo login = (MemberTo) session.getAttribute(AuthConstant.LOGIN_USER);
			if (login!=null)
			{
				MyThreadMsg.setMsg(login);
				return true;
				
			}else
			{
				response.sendRedirect("http://auth.gulimall.com/login.html");
				return false;
			}
		}else
		{
			return true;
		}
		
		
		
		
		
	}
	

}
