/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-09-22 01:37 创建
 *
 */
package com.global.boot.test.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;
import com.global.boot.test.TestBase;
import com.global.boot.test.TestContansts;
import com.global.common.env.Env;
import com.global.common.lang.util.money.Money;
import org.junit.Test;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author qiubo@yiji.com
 */
public class MVCTest extends TestBase {
	@Test
	public void testFastJson() throws Exception {
		ResponseEntity<String> entity = new TestRestTemplate().getForEntity(buildUrl("/testFastjson.json"),
			String.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		JSONObject jsonObject = JSON.parseObject(entity.getBody());
		assertThat(jsonObject.get(TestContansts.FAST_JSON_PROP_NAME)).isNotNull();
	}
	
	@Test
	public void testStaticResouces() throws Exception {
		ResponseEntity<String> entity = new TestRestTemplate().getForEntity(buildUrl("/test3.html"), String.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	
	@Test
	public void testResponseXML() throws Exception {
		ResponseEntity<String> entity = new TestRestTemplate()
			.getForEntity(buildUrl("/testFastjson.xml"), String.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(entity.getBody()).contains("<?xml version=\"1.0\" encoding=\"UTF-8\"").contains("<testBean>")
			.endsWith("</testBean>");
	}
	
	@Test
	public void testFavicon() throws Exception {
		assertThat(HttpRequest.head(buildUrl("favicon.ico")).code()).isEqualTo(200);
	}
	
	@Test
	public void testRemoteIpValve() throws Exception {
		String ip = "113.204.226.234";
		String url = buildUrl("testIp.html");
		assertThat(HttpRequest.get(url).header("X-Forwarded-For", ip).body()).isEqualTo(ip);
		System.out.println("xx");
	}
	
	@Test
	public void testConverter() throws Exception {
		String name = "bohr";
		Money money = new Money("0.01");
		String url = buildUrl("web/testConverter.json?name=" + name + "&money=" + money.toString());
		Pojo pojo = new Pojo();
		pojo.setName(name);
		pojo.setMoney(money);
		assertThat(HttpRequest.get(url).body()).isEqualTo(JSON.toJSONString(pojo));
	}
	
	@Test
	public void testHandlerInterceptor() throws Exception {
		String url = buildUrl("web/testConverter.json?name=bohr&money=0.01");
		HttpRequest.get(url).body();
		assertThat(TestHandlerInterceptor.map).isNotNull();
	}
	
	@Test
	public void testEnv() throws Exception {
		assertGetBodyIs("/boot/env", Env.getEnv());
	}
	
	@Test
	public void testServerName() throws Exception {
		String url = buildUrl("/test3.html");
		Map<String, List<String>> headers = HttpRequest.get(url).headers();
		assertThat(headers.get("Server")).contains("YIJI");
	}
	
	@Test
	public void testResponseHeader() throws Exception {
		String url = buildUrl("/test3.html");
		Map<String, List<String>> headers = HttpRequest.get(url).headers();
		//		assertThat(headers.get("x-frame-options").size()).isEqualTo(1);
		//		assertThat(headers.get("x-frame-options").get(0)).isEqualTo("SAMEORIGIN");
		assertThat(headers.get("x-xss-protection").size()).isEqualTo(1);
		assertThat(headers.get("x-xss-protection").get(0)).isEqualTo("1; mode=block");
		assertThat(headers.get("Content-Security-Policy").size()).isEqualTo(1);
		assertThat(headers.get("Content-Security-Policy").get(0)).isEqualTo("xxxx");
		assertThat(headers.get("xxx").size()).isEqualTo(1);
		assertThat(headers.get("xxx").get(0)).isEqualTo("ooo");
	}
	
	@Test
	public void testInputStream() throws Exception {
		String url = buildUrl("/testGetInput");
		String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<note>\n" + "  <to>Tove</to>\n"
						+ "  <from>Jani</from>\n" + "  <heading>Reminder</heading>\n"
						+ "  <body>Don't forget me this weekend!</body>\n" + "</note>";
		String responseBody = HttpRequest.post(url).send(body).body();
		System.out.println(responseBody);
		assertThat(responseBody).isEqualTo(body);
		url = buildUrl("/testGetInput1");
		responseBody = HttpRequest.post(url).send(body).body();
		assertThat(responseBody).isEqualTo(body);
	}
	
	@Test
	public void testXforwardedProto() throws Exception {
		String url = buildUrl("/testRedirect");
		Map<String, List<String>> map = HttpRequest.get(url).header("x-forwarded-proto", "https").headers();
		assertThat(map.get("Location")).contains("https://127.0.0.1/");
		
	}
	
	@Test
	public void testWelcomeFile() throws Exception {
		assertGetBodyContains("/", "new welcome");
	}
}
