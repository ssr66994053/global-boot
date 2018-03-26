/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-02-18 18:47 创建
 */
package com.global.boot.test.filterchain.test2;

import com.global.boot.filterchain.Filter;
import com.global.boot.filterchain.FilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qiubo@yiji.com
 */
public class TestFilter3 implements Filter<TestContext2> {
	private static final Logger logger = LoggerFactory.getLogger(TestFilter3.class);
	
	@Override
	public void doFilter(TestContext2 context, FilterChain<TestContext2> filterChain) {
		logger.info("in");
		filterChain.doFilter(context);
		logger.info("out");
	}
	
}
