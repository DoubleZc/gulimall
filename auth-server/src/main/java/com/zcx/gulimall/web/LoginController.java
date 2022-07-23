package com.zcx.gulimall.web;


import com.zcx.common.constant.AuthConstant;
import com.zcx.common.utils.ExceptionCode;
import com.zcx.common.utils.R;
import com.zcx.gulimall.feign.ThiredFeignSerice;
import com.zcx.gulimall.utils.ValidateCodeUtils;
import com.zcx.gulimall.vo.UserRegistVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;


@Controller
@Slf4j
public class LoginController
{

	@Autowired
	ThiredFeignSerice thiredFeignSerice;

	@Autowired
	StringRedisTemplate redisTemplate;

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
	public String regist(@Validated UserRegistVo vo, Model model)
	{




		return "redirect:/login.html";
	}



}
