package com.zcx.gulimall.vo.github;


import lombok.Data;

@Data
public class GithubToken
{
	private String access_token;
	private String token_type;
	private String scope;

}
