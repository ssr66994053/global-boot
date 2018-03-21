/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-02-18 18:47 创建
 */
package com.yiji.boot.test.filterchain.test1;

import com.yiji.boot.filterchain.Filter;
import com.yiji.boot.filterchain.FilterChain;
import com.yiji.boot.test.filterchain.service.TestComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author qiubo@yiji.com
 */
public class TestFilter1 implements Filter<TestContext1> {
	private static final Logger logger = LoggerFactory.getLogger(TestFilter1.class);
	
	@Autowired(required = false)
	private TestComponent testComponent;
	
	@PostConstruct
	public void init() {
		logger.info("init");
	}
	
	@Override
	public void doFilter(TestContext1 context, FilterChain<TestContext1> filterChain) {
		context.addMsg("test1 in");
		context.addMsg("component is null:" + Boolean.toString(testComponent == null));
		if (context.getName().equals("break")) {
			throw new RuntimeException("break");
		}
		filterChain.doFilter(context);
		context.addMsg("test1 out");
	}
	
	@PreDestroy
	public void destroy() {
		logger.info("destroy");
	}
	
	@Override
	public int getOrder() {
		return 1;
	}
	
	@Override
	public String getName() {
		return "测试过滤器1";
	}
}
