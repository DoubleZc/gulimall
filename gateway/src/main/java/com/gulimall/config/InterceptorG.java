package com.gulimall.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
//@Component
//@Order(-100000)
public class InterceptorG  implements GlobalFilter
{
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
	{
		String token = exchange.getRequest().getHeaders().getFirst("token");
		if (Strings.isEmpty(token))
		{
			
			ServerHttpResponse response = exchange.getResponse();
			response.getHeaders().set(HttpHeaders.LOCATION,"http://www.baidu.com");
		 return response.setComplete();
			
		
		}else
		{
			log.info(exchange.getRequest().getPath().value());
			return chain.filter(exchange);
		}
	}
}
