/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-09-18 14:33 创建
 *
 */
package com.yiji.boot.test.web;

import com.github.kevinsawicki.http.HttpRequest;
import com.yiji.boot.test.TestBase;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author qiubo@yiji.com
 */
public class ExceptionTest extends TestBase {
	@Test
	public void testExceptionJson() throws Exception {
		assertGetBodyIs("web/testExceptionJson.json", "{\"message\":\"xxx\",\"success\":false}");
	}
	
	@Test
	public void testExceptionJsonWithMessageable() throws Exception {
		assertGetBodyIs("web/testExceptionJsonWithMessageable.json",
			"{\"code\":\"code\",\"message\":\"message\",\"success\":false}");
	}
	
	@Test
	public void testExceptionHtml() throws Exception {
		assertGetBodyContains("web/testExceptionHtml.html", "<h1>errorPage</h1>");
	}
	
	@Test
	public void testExceptionHtmlWithResponseStatus() throws Exception {
		String url = buildUrl("web/testExceptionHtmlWithResponseStatus.html");
		HttpRequest request = HttpRequest.get(url);
		String body = request.body();
		int code = request.code();
		assertThat(body).contains("<h1>errorPage</h1>");
		assertThat(code).isEqualTo(HttpStatus.BAD_GATEWAY.value());
		assertThat(body).contains("message=" + WebController.ResponseStatusException.REASON);
		assertThat(body).contains("exception=com.yiji.boot.test.web.WebController$ResponseStatusException");
		
	}
}
