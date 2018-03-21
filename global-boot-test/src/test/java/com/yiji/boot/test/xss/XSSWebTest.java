/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-12 09:49 创建
 *
 */
package com.yiji.boot.test.xss;

import com.github.kevinsawicki.http.HttpRequest;
import com.yiji.boot.test.TestBase;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author qiubo@yiji.com
 */
public class XSSWebTest extends TestBase {
	@Test
	public void testXSS() throws Exception {
		String xss = "<img%20src%3D%26%23x6a;%26%23x61;%26%23x76;%26%23x61;%26%23x73;%26%23x63;%26%23x72;%26%23x69;%26%23x70;%26%23x74;%26%23x3a;alert%26%23x28;27111%26%23x29;>";
		String expect = "\"<img src=alert(27111)>\"";
		String url = buildUrl("testXss.json?input=" + xss);
		assertThat(HttpRequest.get(url).body()).isEqualTo(expect);
	}
	
	@Test
	public void testXSS1() throws Exception {
		String xss = "<img%20src%3D%26%23x6a;%26%23x61;%26%23x76;%26%23x61;%26%23x73;%26%23x63;%26%23x72;%26%23x69;%26%23x70;%26%23x74;%26%23x3a;alert%26%23x28;27111%26%23x29;>";
		String expect = "<img src=alert(27111)>";
		String url = buildUrl("testXss.html?input=" + xss);
		assertThat(HttpRequest.get(url).body()).isEqualTo(expect);
	}
	
}
