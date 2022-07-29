package com.zcx.gulimall.web;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.EncryptUtils;
import com.zcx.common.constant.AuthConstant;
import com.zcx.common.to.MemberTo;
import com.zcx.common.utils.ExceptionCode;
import com.zcx.common.utils.R;
import com.zcx.gulimall.feign.MemberFeignService;
import com.zcx.gulimall.feign.ThiredFeignSerice;
import com.zcx.gulimall.utils.ValidateCodeUtils;
import com.zcx.gulimall.vo.UserLoginVo;
import com.zcx.gulimall.vo.UserRegistVo;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.generators.SCrypt;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.crypto.spec.PBEKeySpec;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Controller
@Slf4j
public class LoginController
{

	@Autowired
	ThiredFeignSerice thiredFeignSerice;

	@Autowired
	StringRedisTemplate redisTemplate;

	@Autowired
	RedissonClient redissonClient;


	@Autowired
	MemberFeignService memberFeignService;

	@ResponseBody
	@GetMapping("/sms/sendcode")
	public R sendCode(@RequestParam("phone") String phone)
	{
		//TODO 接口防刷

		//在缓存中查看有无验证码信息
		String oldCode = redisTemplate.opsForValue().get(AuthConstant.SMS_CODE_PREFIX + phone);
		if (oldCode!=null){

			//看发送验证码请求是否频繁
			long oldTime = Long.parseLong(oldCode.split("_")[1]);
			if (System.currentTimeMillis()-oldTime<60000)
			{
				//频繁返回
				return R.error(ExceptionCode.SMS_CODE_EXCEPTION);
			}
			//非频繁再次发送验证码
		}
		//没有验证码信息获取验证码
		String code = String.valueOf(ValidateCodeUtils.generateValidateCode(4));
		//发送验证码
		R r = thiredFeignSerice.sendSmsCode(phone, code);
		//存入redis
		if (r.getCode() == 0) {
			redisTemplate.opsForValue().set(AuthConstant.SMS_CODE_PREFIX+phone,code+"_"+System.currentTimeMillis(),5, TimeUnit.MINUTES);
		}
		return r;
	}



	@PostMapping("/regist")
	public String regist(@Validated UserRegistVo vo, BindingResult bindingResult
	, RedirectAttributes redirectAttributes)
	{


		if (bindingResult.hasErrors())
		{
			List<FieldError> fieldErrors = bindingResult.getFieldErrors();
			Map<String, String> errors = fieldErrors.stream().collect(Collectors.toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage));
			redirectAttributes.addFlashAttribute("errors",errors);
			return  "redirect:http://auth.gulimall.com/reg.html";
		}

		String code = vo.getCode();
		RLock codeLock = redissonClient.getLock("codeLock");

		//分布式锁
		codeLock.lock();
		String codeTime = redisTemplate.opsForValue().get(AuthConstant.SMS_CODE_PREFIX + vo.getMobile());
		if (codeTime!=null &&codeTime.split("_")[0].equals(code))
		{
			//删除验证码
			redisTemplate.delete(AuthConstant.SMS_CODE_PREFIX + vo.getMobile());
			codeLock.unlock();
			//验证码正确
			//远程调用注册
			R r = memberFeignService.save(vo);
			if (r.getCode()==0)
			{
				redirectAttributes.addFlashAttribute("code", 0);
				return "redirect:http://auth.gulimall.com/login.html";
			}else
			{
				redirectAttributes.addFlashAttribute("errors", r);
				return "redirect:http://auth.gulimall.com/reg.html";
			}

		}else {
			codeLock.unlock();
			Map<String, String> errors = new HashMap<>();
			errors.put("code", "验证码错误");
			redirectAttributes.addFlashAttribute("errors", errors);
			return "redirect:http://auth.gulimall.com/reg.html";
		}
	}


	@PostMapping("/login")
	public String login(@Validated UserLoginVo vo,RedirectAttributes attributes, HttpSession session)
	{
		log.warn("登录:账号{},密码{}",vo.getUsername(),vo.getPassword());
		R login=new R();
		try {
			 login = memberFeignService.login(vo);
			if (login.getCode()==0)
			{
				MemberTo data = JSON.parseObject(JSON.toJSONString(login.get("data")), MemberTo.class);
				session.setAttribute(AuthConstant.LOGIN_USER,data);
				return "redirect:http://gulimall.com";

			}

		} catch (Exception e) {
			login = R.error(ExceptionCode.FEIGN_EXCEPTION);
		}
		attributes.addFlashAttribute("errors",login);
		return "redirect:http://auth.gulimall.com/login.html";


	}



	@GetMapping("/login.html")
	public String loginPage(HttpSession session)
	{

		Object attribute = session.getAttribute(AuthConstant.LOGIN_USER);
		if (attribute==null)
		{

			return "login";
		}else
		{
			return "redirect:http://gulimall.com";
		}



	}

}
