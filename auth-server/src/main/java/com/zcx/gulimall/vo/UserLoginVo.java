package com.zcx.gulimall.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
public class UserLoginVo
{
	/**
	 * 用户名
	 */
	@NotBlank(message = "用户名必须提交")
	private String username;
	/**
	 * 密码
	 */
	@NotBlank(message = "密码不能为空")
	private String password;

}
