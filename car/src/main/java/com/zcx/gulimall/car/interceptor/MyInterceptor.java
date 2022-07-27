package com.zcx.gulimall.car.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.zcx.common.constant.AuthConstant;
import com.zcx.common.constant.CartConstant;
import com.zcx.common.constant.ComConstant;
import com.zcx.common.to.MemberTo;
import com.zcx.common.utils.MyThreadMsg;
import com.zcx.gulimall.car.vo.UserTo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;


@Component
public class MyInterceptor implements HandlerInterceptor
{
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		UserTo userTo = new UserTo();
		HttpSession session = request.getSession();
		MemberTo member = (MemberTo) session.getAttribute(AuthConstant.LOGIN_USER);
		if (member != null) {
			//session有登录信息
			userTo.setId(member.getId());
	
		}
		
		//session没有登录信息，检查cookie中有没有临时信息
		
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(CartConstant.COOKIE_USER_KEY)) {
				//cookie中有临时信息,存入userTo中
					userTo.setUserKey(cookie.getValue());
					userTo.setTemple(true);
					MyThreadMsg.setMsg(userTo);
					return true;
					
				}
			}
			
		}
		//cookie中没有临时信息，创建领是信息
		UUID uuid = UUID.randomUUID();
		userTo.setUserKey(uuid.toString());
		userTo.setTemple(false);
		
		MyThreadMsg.setMsg(userTo);
		return true;
	}
	
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception
	{
		UserTo user = MyThreadMsg.getMsg(UserTo.class);
		if (!user.isTemple())
		{
			
			Cookie cookie = new Cookie(CartConstant.COOKIE_USER_KEY,user.getUserKey());
			cookie.setDomain(ComConstant.SESSION_DOMAIN);
			cookie.setMaxAge(CartConstant.COOKIE_USER_KEY_TIEMOUT);
			response.addCookie(cookie);
			
		}
		
		
		
	}
}
