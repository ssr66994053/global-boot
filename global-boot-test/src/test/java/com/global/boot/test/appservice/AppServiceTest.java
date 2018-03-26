/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-10-11 11:38 创建
 */
package com.global.boot.test.appservice;

import com.github.kevinsawicki.http.HttpRequest;
import com.global.boot.test.TestBase;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author qiubo@yiji.com
 */

public class AppServiceTest extends TestBase {
	
	@Test
	public void test() throws Exception {
		String resp = HttpRequest.get(buildUrl("app/test.json")).body();
		assertThat(resp).contains("\"success\":false");
		assertThat(resp).contains("\"code\":\"comn_04_0003\"");
		assertThat(resp).contains("playload.a2:不能为空");
	}
	
	@Test
	public void test_success() throws Exception {
		String resp = HttpRequest.get(buildUrl("app/test.json?a2=x")).body();
		assertThat(resp).contains("\"success\":true");
	}
	
	@Test
	public void test1() throws Exception {
		String resp = HttpRequest.get(buildUrl("app/test1.json")).body();
		assertThat(resp).contains("\"success\":false");
		assertThat(resp).contains("\"code\":\"comn_04_0003\"");
		assertThat(resp).contains("playload.a2:不能为空");
		assertThat(resp).contains("playload.a1:不能为空");
		assertThat(resp).contains("playload.a4:不能为空");
	}
	
	@Test
	public void test1_checkMethod() throws Exception {
		String resp = HttpRequest.get(buildUrl("app/test1.json?a1=axx")).body();
		assertThat(resp).contains("\"success\":false");
		assertThat(resp).contains("\"code\":\"comn_04_0003\"");
		assertThat(resp).contains("a1:不能以a开头");
	}
	
	@Test
	public void test2() throws Exception {
		String resp = HttpRequest.get(buildUrl("app/test2.json")).body();
		assertThat(resp).contains("\"success\":false");
		assertThat(resp).contains("\"code\":\"comn_04_0003\"");
		assertThat(resp).contains("playload.a2:不能为空");
		assertThat(resp).contains("playload.a3:不能为空");
		assertThat(resp).contains("playload.a4:不能为空");
		
	}
	
	@Test
	public void test3() throws Exception {
		String resp = HttpRequest.get(buildUrl("app/test3.json")).body();
		assertThat(resp).contains("\"success\":false");
		assertThat(resp).contains("\"code\":\"comn_04_0003\"");
		assertThat(resp).doesNotContain("playload.a2:不能为空");
		assertThat(resp).contains("playload.a3:不能为空");
		assertThat(resp).contains("playload.a4:不能为空");
	}
	
}
