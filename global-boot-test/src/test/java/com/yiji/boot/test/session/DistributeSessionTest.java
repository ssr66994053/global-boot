/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-24 13:32 创建
 *
 */
package com.yiji.boot.test.session;

import com.github.kevinsawicki.http.HttpRequest;
import com.yiji.boot.core.Apps;
import com.yiji.boot.test.TestBase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yanglie@yiji.com
 */
public class DistributeSessionTest extends TestBase {
	@Test
	public void testDistributeSession() throws Exception {
		String sessionId = getSessionId();
		String url = buildUrl("session_save.html?key=yanglie&value=test");
		String result = HttpRequest.get(url).header("Cookie", Apps.getAppSessionCookieName() + "=" + sessionId).body();
		assertThat(result)
			.isEqualTo(
				"class org.springframework.session.web.http.SessionRepositoryFilter$SessionRepositoryRequestWrapper$HttpSessionWrapper");
		
		url = buildUrl("session_read.html?key=yanglie");
		result = HttpRequest.get(url).header("Cookie", Apps.getAppSessionCookieName() + "=" + sessionId).body();
		assertThat(result).isEqualTo("test");
	}
	
	private String getSessionId() throws IOException {
		Document doc = Jsoup.connect(buildUrl("/testvm1.html")).timeout(600000).get();
		Element sessionIdElement = doc.getElementsByTag("sessionId").first();
		String sessionId = sessionIdElement.html();
		return sessionId;
	}
	
	@Test
	public void testSessionFromUrl() throws Exception {
		String sessionId = getSessionId();
		String url = buildUrl("session_save.html?key=yanglie&value=test&" + Apps.getAppSessionCookieName() + "="
								+ sessionId);
		String result = HttpRequest.get(url).body();
		assertThat(result)
			.isEqualTo(
				"class org.springframework.session.web.http.SessionRepositoryFilter$SessionRepositoryRequestWrapper$HttpSessionWrapper");
		
		url = buildUrl("session_read.html?key=yanglie&" + Apps.getAppSessionCookieName() + "=" + sessionId);
		result = HttpRequest.get(url).body();
		assertThat(result).isEqualTo("test");
	}
	
	@Test
	public void testMultiLogin() throws Exception {
		String url = buildUrl("session_login?name=bohr");
		HttpRequest.get(url).body();
		assertThat(HttpRequest.get(url).body()).contains("UserHasLoginException");
	}
}
