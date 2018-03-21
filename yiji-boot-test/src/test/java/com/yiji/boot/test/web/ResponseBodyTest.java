/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-09-25 14:46 创建
 *
 */
package com.yiji.boot.test.web;

import com.github.kevinsawicki.http.HttpRequest;
import com.yiji.boot.test.TestBase;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author qiubo@yiji.com
 */
public class ResponseBodyTest extends TestBase {
	@Test
	public void testResponseBody_text() throws Exception {
		String url = buildUrl("/web/testResponseBody");
		assertThat(HttpRequest.get(url).body()).isEqualTo("hello");
	}
	
	@Test
	public void testResponseBody_text1() throws Exception {
		String url = buildUrl("/web/testResponseBody.html");
		assertThat(HttpRequest.get(url).body()).isEqualTo("hello");
	}
	
	@Test
	public void testResponseBody_json() throws Exception {
		String url = buildUrl("/web/testResponseBody.json");
		assertThat(HttpRequest.get(url).body()).isEqualTo("\"hello\"");
	}
	
	@Test
	public void testResponseBody_json1() throws Exception {
		String url = buildUrl("/web/testResponseBody");
		assertThat(HttpRequest.get(url).accept("application/json").body()).isEqualTo("\"hello\"");
	}
	
	@Test
	public void testResponseBodyPojo_text() throws Exception {
		String url = buildUrl("/web/testResponseBodyPojo");
		assertThat(HttpRequest.get(url).body()).contains("\"name\":\"xxx\"");
	}
	
	@Test
	public void testResponseBodyPojo_text1() throws Exception {
		String url = buildUrl("/web/testResponseBodyPojo.html");
		assertThat(HttpRequest.get(url).body()).contains("Could not find acceptable representation");
	}
	
	@Test
	public void testResponseBodyPojo_json() throws Exception {
		String url = buildUrl("/web/testResponseBodyPojo.json");
		assertThat(HttpRequest.get(url).body()).contains("\"name\":\"xxx\"");
	}
	
	@Test
	public void testResponseBodyPojo_json1() throws Exception {
		String url = buildUrl("/web/testResponseBodyPojo");
		assertThat(HttpRequest.get(url).accept("application/json").body()).contains("\"name\":\"xxx\"");
	}
	
}
