package com.zcx.common.config.seata;


import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

//@Configuration
//public class MySeataConfig
//{
//	@Bean
//	public DataSource dataSource(DataSourceProperties properties)
//	{
//		HikariDataSource source = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
//		if (StringUtils.hasText(properties.getName())) {
//			source.setPoolName(properties.getName());
//		}
//		return new DataSourceProxy(source);
//	}
//}
