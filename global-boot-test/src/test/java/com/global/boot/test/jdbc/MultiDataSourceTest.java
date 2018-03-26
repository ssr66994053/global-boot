/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-02-29 14:39 创建
 */
package com.global.boot.test.jdbc;

import com.global.boot.jdbc.DruidProperties;
import com.global.boot.test.TestBase;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author qiubo@yiji.com
 */
public class MultiDataSourceTest extends TestBase {
	
	@Configuration
	public static class Config {
		@Bean
		public DataSource testDataSource() {
			return DruidProperties.buildFromEnv("yiji.ds1");
		}
	}
	
	@Resource(name = "dataSource")
	private DataSource dataSource;
	@Resource(name = "testDataSource")
	private DataSource testDataSource;
	
	@Test
	public void testName() throws Exception {
		assertThat(dataSource).isNotNull();
		assertThat(testDataSource).isNotNull();
	}
}
