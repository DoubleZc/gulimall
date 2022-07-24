package com.zcx.gulimall.vo;


import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class UserRegistVo
{
	/**
	 * 用户名
	 */
	@NotNull(message = "用户名必须提交")
	@Length(min =6,max = 18,message = "用户名长度在6-18之间")
	private String username;
	/**
	 * 密码
	 */
	@NotNull(message = "密码不能为空")
	@Length(min =6,max = 18,message = "密码长度在6-18之间")
	private String password;
	/**
	 * 手机号码
	 */
	@NotNull
	@Pattern(regexp = "^1(3[0-9]|4[01456879]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[0-35-9])\\d{8}$" ,message = "手机号格式不正确")
	private String mobile;
	/**
	 * 验证码
	 */
	@NotNull
	@Pattern(regexp = "^\\d{4}$",message = "验证码格式不正确")
	private  String code;




}
