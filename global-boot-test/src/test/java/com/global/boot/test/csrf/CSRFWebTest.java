/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-25 17:06 创建
 *
 */
package com.global.boot.test.csrf;

import com.github.kevinsawicki.http.HttpRequest;
import com.global.boot.core.Apps;
import com.global.boot.test.TestBase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author qiubo@yiji.com
 */

public class CSRFWebTest extends TestBase {
	private static final String CSRF_DENIED_STR = "{\"code\":\"CSRF_DENIED\",\"message\":\"Expected CSRF token not found.\",\"success\":false}";
	
	@Test
	public void testCsrfJsonRequest_uri() throws Exception {
		String url = buildUrl("testFastjson.json?dsafasf=sdafds");
		assertThat(HttpRequest.post(url).body()).isEqualTo(CSRF_DENIED_STR);
	}
	
	@Test
	public void testCsrfJsonRequest_header() throws Exception {
		String url = buildUrl("testFastjson");
		assertThat(HttpRequest.post(url).accept("application/json").body()).isEqualTo(CSRF_DENIED_STR);
	}
	
	@Test
	public void testInvalidateToken() throws Exception {
		//load token
		Document doc = Jsoup.connect(buildUrl("/testvm1.html")).timeout(600000).get();
		Element sessionIdElement = doc.getElementsByTag("sessionId").first();
		String sessionId = sessionIdElement.html();
		String body = HttpRequest.post(buildUrl("testFastjson.json"))
			.header("Cookie", Apps.getAppSessionCookieName() + "=" + sessionId).header("X-CSRF-TOKEN", "xxxx")
			.accept("application/json").body();
		assertThat(body).contains("CSRF_DENIED");
	}
	
	@Test
	public void testCsrfHtmlRequest() throws Exception {
		String url = buildUrl("/");
		String body = HttpRequest.post(url).body();
		assertThat(body).contains("<meta name=\"X-CSRF-TOKEN\" content=");
		assertThat(body).contains("Expected CSRF token not found.");
	}
	
	@Test
	public void testCsrfHtmlRequest_cookie() throws Exception {
		String url = buildUrl("/test3.html");
		Map<String, List<String>> headers = HttpRequest.get(url).headers();
		assertThat(headers).containsKey("Set-Cookie");
		assertThat(headers.get("Set-Cookie")).isNotEmpty();
		assertThat(headers.get("Set-Cookie").stream().anyMatch(s -> s.startsWith("_csrf"))).isTrue();
	}
	
	@Test
	public void testSuccess() throws Exception {
		String url = buildUrl("/test3.html");
		Map<String, List<String>> headers = HttpRequest.get(url).headers();
		String csrf = getCsrfVlaueFromCookie(headers);
		String sessionId = getSessionIdVlaueFromCookie(headers);
		String body = HttpRequest.post(buildUrl("testFastjson.json"))
			.header("Cookie", Apps.getAppSessionCookieName() + "=" + sessionId).header("Cookie", "_csrf=" + csrf)
			.accept("application/json").body();
		System.out.println(body);
		
	}
	
	@Test
	public void testDuplicatedCsrfCookie() throws Exception {
		String url = buildUrl("/test3.html");
		Map<String, List<String>> headers = HttpRequest.get(url).headers();
		String csrf = getCsrfVlaueFromCookie(headers);
		url = buildUrl("/login.html");
		headers = HttpRequest.get(url).header("Cookie", "_csrf=" + csrf).headers();
		System.out.println(headers);
		assertThat(getCsrfVlaueFromCookie(headers)).isNull();
	}
	
	private String getCsrfVlaueFromCookie(Map<String, List<String>> headers) {
		String csrf = null;
		if (headers.get("Set-Cookie") == null) {
			return null;
		}
		for (String cookie : headers.get("Set-Cookie")) {
			if (cookie.contains("_csrf=")) {
				csrf = cookie.replace("_csrf=", "");
			}
		}
		return csrf;
	}
	
	private String getSessionIdVlaueFromCookie(Map<String, List<String>> headers) {
		String csrf = null;
		if (headers.get("Set-Cookie") == null) {
			return null;
		}
		for (String cookie : headers.get("Set-Cookie")) {
			if (cookie.contains(Apps.getAppSessionCookieName())) {
				csrf = cookie.replace(Apps.getAppSessionCookieName() + "=", "");
			}
		}
		return csrf;
	}
}
