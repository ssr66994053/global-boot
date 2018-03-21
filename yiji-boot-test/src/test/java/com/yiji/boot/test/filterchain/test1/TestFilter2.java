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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qiubo@yiji.com
 */
public class TestFilter2 implements Filter<TestContext1> {
	private static final Logger logger = LoggerFactory.getLogger(TestFilter2.class);
	
	@Override
	public void doFilter(TestContext1 context, FilterChain<TestContext1> filterChain) {
		context.addMsg("test2 in");
		filterChain.doFilter(context);
		context.addMsg("test2 out");
	}
	
	@Override
	public int getOrder() {
		return 0;
	}
}
