package com.gulimall.config;

import org.apache.logging.log4j.util.Strings;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.cloud.gateway.handler.predicate.QueryRoutePredicateFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 路由断言工厂
 * 类名必须有RoutePredicateFactory
 *
 *
 */
@Component
public class CusRoutePredicateFactory extends AbstractRoutePredicateFactory<CusRoutePredicateFactory.Config>
{
	
	public static final String PARAM_KEY = "param";
	
	/**
	 * Regexp key.
	 */
	public static final String REGEXP_KEY = "regexp";
	
	public CusRoutePredicateFactory() {
		super(CusRoutePredicateFactory.Config.class);
	}
	
	@Override
	public List<String> shortcutFieldOrder() {
		return Arrays.asList(PARAM_KEY, REGEXP_KEY);
	}
	
	@Override
	public Predicate<ServerWebExchange> apply(CusRoutePredicateFactory.Config config) {
		return new GatewayPredicate() {
			@Override
			public boolean test(ServerWebExchange exchange) {
				
				String first = exchange.getRequest().getHeaders().getFirst(config.param);
				return Strings.isEmpty(first);
			}
			
		
		};
	}
	
	@Validated
	public static class Config {
		
		@NotEmpty
		private String param;
		
		private String regexp;
		
		public String getParam() {
			return param;
		}
		
		public CusRoutePredicateFactory.Config setParam(String param) {
			this.param = param;
			return this;
		}
		
		public String getRegexp() {
			return regexp;
		}
		
		public CusRoutePredicateFactory.Config setRegexp(String regexp) {
			this.regexp = regexp;
			return this;
		}
		
	}
	
}
