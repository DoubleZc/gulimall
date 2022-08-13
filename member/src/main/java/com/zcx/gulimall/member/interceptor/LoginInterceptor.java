package com.zcx.gulimall.member.interceptor;


import com.zcx.common.constant.AuthConstant;
import com.zcx.common.to.MemberTo;
import com.zcx.common.utils.MyThreadMsg;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor
{
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		String requestURI = request.getRequestURI();
		if (!requestURI.contains("memberOrder")){
			
			return true;
		}
		
		
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
		
	}
	
}
