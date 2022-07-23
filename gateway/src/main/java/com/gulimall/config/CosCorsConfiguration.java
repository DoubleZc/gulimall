package com.gulimall.config;


import com.alibaba.nacos.common.http.HttpUtils;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CosCorsConfiguration
{


	@Bean
	public CorsWebFilter corsWebFilter()
	{

		CorsConfiguration corsConfiguration = new CorsConfiguration();
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		//允许哪个头
		corsConfiguration.addAllowedHeader("*");
		//       允许哪个请求方法
		corsConfiguration.addAllowedMethod("*");
		//       允许哪个来源
		corsConfiguration.addAllowedOriginPattern("*");
		//       是否允许携带cookie
		corsConfiguration.setAllowCredentials(true);

		source.registerCorsConfiguration("/**", corsConfiguration);


		return new CorsWebFilter(source);


	}


}
