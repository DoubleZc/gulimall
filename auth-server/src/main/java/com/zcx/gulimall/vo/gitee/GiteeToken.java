package com.zcx.gulimall.vo.gitee;

import lombok.Data;

@Data
public class GiteeToken
{
	private String access_token;
	private String token_type;
	private Long expires_in;
	private String refresh_token;
	private String scope;
	private Long created_at;
}
