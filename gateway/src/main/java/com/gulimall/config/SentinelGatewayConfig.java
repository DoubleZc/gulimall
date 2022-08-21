package com.gulimall.config;


import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.fastjson.JSON;
import com.zcx.common.utils.ExceptionCode;
import com.zcx.common.utils.R;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class SentinelGatewayConfig
{
	
	public SentinelGatewayConfig()
	{
		
		GatewayCallbackManager.setBlockHandler(new BlockRequestHandler()
		{
			//网关限流请求
			@Override
			public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t)
			{
				R error = R.error(ExceptionCode.TOO_MANY_REQUEST);
				String s = JSON.toJSONString(error);
				return ServerResponse.ok().body(Mono.just(s), String.class);
			}
		});
		
	}
	
	
	
}
