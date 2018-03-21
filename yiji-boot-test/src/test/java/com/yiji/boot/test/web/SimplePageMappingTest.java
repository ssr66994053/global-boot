/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-10-19 18:09 创建
 *
 */
package com.yiji.boot.test.web;

import com.yiji.boot.test.TestBase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yanglie@yiji.com
 */
public class SimplePageMappingTest extends TestBase {
	@Value("${local.server.port}")
	protected int port;
	
	@Test
	public void testMapping() throws IOException {
		String baseUrl = "http://127.0.0.1:" + this.port;
		Document doc = Jsoup.connect(baseUrl + "/xxIndex.html").timeout(60000).get();
		Element title = doc.select("title").first();
		assertThat(title).isNotNull();
		String titleValue = title.html();
		assertThat(titleValue).isNotEmpty();
	}
}
