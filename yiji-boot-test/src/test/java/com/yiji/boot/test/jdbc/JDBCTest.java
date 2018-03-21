/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-24 19:00 创建
 *
 */
package com.yiji.boot.test.jdbc;

import com.yiji.boot.test.TestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author qiubo@yiji.com
 */
public class JDBCTest extends TestBase {
	@Autowired(required = false)
	private DataSource dataSource;
	@Autowired(required = false)
	private JdbcTemplate jdbcTemplate;
	@Autowired(required = false)
	private PlatformTransactionManager platformTransactionManager;
	
	@Autowired(required = false)
	private TransactionTemplate transactionTemplate;
	
	@Test
	public void testDataSource() throws Exception {
		assertThat(dataSource).isNotNull();
	}
	
	@Test
	public void testJdbcTemplate() throws Exception {
		List<Map<String, Object>> list = jdbcTemplate.queryForList("select 'x'");
		assertThat(list).hasSize(1);
	}
	
	@Test
	public void testPlatformTransactionManager() throws Exception {
		assertThat(platformTransactionManager).isNotNull();
	}
	
	@Test
	public void testTransactionTemplate() throws Exception {
		assertThat(transactionTemplate).isNotNull();
	}
}
