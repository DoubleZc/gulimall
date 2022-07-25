package com.zcx.gulimall.utils;

import org.apache.http.HttpResponse;

public  interface OAuthUtils
{
	 HttpResponse getToken(String code) throws Exception;
	 HttpResponse getUser(String access_token) throws Exception;
}



