/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-02-18 18:06 创建
 */
package com.global.boot.test.filterchain;

import com.global.boot.filterchain.FilterChain;
import com.global.boot.test.TestBase;
import com.global.boot.test.filterchain.test1.TestContext1;
import com.global.boot.test.filterchain.test1.TestFilterChain1;
import com.global.boot.test.filterchain.test2.TestContext2;
import com.global.boot.test.filterchain.test2.TestFilterChain2;
import org.junit.Test;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author qiubo@yiji.com
 */
public class FilterTest extends TestBase {
	@Resource(name = "testFilterChain1")
	private FilterChain<TestContext1> filter1;
	@Resource(name = "testFilterChain2")
	private FilterChain<TestContext2> filter2;
	
	@Test
	public void testAutoWire() throws Exception {
		assertThat(filter1).isInstanceOf(TestFilterChain1.class);
		assertThat(filter2).isInstanceOf(TestFilterChain2.class);
	}
	
	@Test
	public void testSuccess() throws Exception {
		TestContext1 testContext1 = new TestContext1();
		filter1.doFilter(testContext1);
		assertThat(testContext1.getMsgs()).hasSize(5).containsExactly("test2 in", "test1 in",
			"component is null:false", "test1 out", "test2 out");
	}
	
	@Test
	public void testReentry() throws Exception {
		TestContext1 testContext1 = new TestContext1();
		filter1.doFilter(testContext1);
		assertThat(testContext1.getMsgs()).hasSize(5);
		
		filter1.doFilter(testContext1);
		assertThat(testContext1.getMsgs()).hasSize(5);
		
		testContext1.reentry();
		filter1.doFilter(testContext1);
		assertThat(testContext1.getMsgs()).hasSize(10);
		
	}
	
	@Test
	public void testEx() throws Exception {
		TestContext1 testContext1 = new TestContext1();
		testContext1.setName("break");
		try {
			filter1.doFilter(testContext1);
		} catch (Exception e) {
			assertThat(testContext1.getMsgs()).hasSize(3).containsExactly("test2 in", "test1 in",
				"component is null:false");
		}
	}
	
}
