/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-24 14:32 创建
 *
 */
package com.global.boot.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Lists;
import com.global.boot.core.AppConfigException;
import com.global.common.portrait.model.DBEndpoint;
import com.global.common.portrait.model.IOResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * @author qiubo@yiji.com
 */
@Configuration
@EnableConfigurationProperties({ DruidProperties.class })
@ConditionalOnProperty(value = "yiji.ds.enable", matchIfMissing = true)
public class JDBCAutoConfiguration implements IOResource<DBEndpoint> {
	private static final Logger logger = LoggerFactory.getLogger(JDBCAutoConfiguration.class);
	
	@Autowired
	private DruidProperties druidProperties;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Bean
	public DataSource dataSource() {
		try {
			if (druidProperties == null) {
				return DruidProperties.buildFromEnv(DruidProperties.PREFIX);
			} else {
				return druidProperties.build();
			}
		} catch (Exception e) {
			//这种方式有点挫，先就这样吧
			logger.error("初始化数据库连接池异常，关闭应用", e);
			System.exit(0);
			throw new AppConfigException("数据源配置异常", e);
		}
	}
	
	@Bean
	@ConditionalOnMissingBean(JdbcOperations.class)
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
	
	@Bean
	@ConditionalOnMissingBean(NamedParameterJdbcOperations.class)
	public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}
	
	@Bean
	@ConditionalOnMissingBean(TransactionTemplate.class)
	public TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager) {
		return new TransactionTemplate(platformTransactionManager);
	}
	
	@Override
	public List<DBEndpoint> endpoints() {
		List<DBEndpoint> dbEndpoints = Lists.newArrayList();
		Map<String, DruidDataSource> beansOfType = applicationContext.getBeansOfType(DruidDataSource.class);
		for (DruidDataSource dataSource : beansOfType.values()) {
			String url = dataSource.getUrl();
			DBEndpoint dbEndpoint = DruidProperties.parseJDBCUrl(url);
			dbEndpoint.setUsername(dataSource.getUsername());
			dbEndpoints.add(dbEndpoint);
		}
		return dbEndpoints;
	}
}
