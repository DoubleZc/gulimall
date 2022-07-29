package com.zcx.common.config.thread;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix ="gulimall.my.thread")
@Data
public class ThreadPoolProperties
{
	private Integer coreSize=3;
	private Integer maximumPoolSize=100;
	private long keepAliveTime=10;
	private Integer QueueSize=1000;

}
