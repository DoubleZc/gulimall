package com.zcx.gulimall.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "gulimall.oauth")
@Component
@Data
public class OAuthProperties
{
	private String client_id;
	private String client_secret;

}
