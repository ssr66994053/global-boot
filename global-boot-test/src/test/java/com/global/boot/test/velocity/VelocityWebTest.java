/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-01-01 19:06 创建
 *
 */
package com.global.boot.test.velocity;

import com.global.boot.test.TestBase;
import com.global.boot.velocity.HtmlEscapeReference;
import com.global.boot.velocity.YijiVelocityProperties;
import com.yjf.common.spring.ApplicationContextHolder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 测试velocity crsf json支持
 *
 * @author qzhanbo@yiji.com
 */

public class VelocityWebTest extends TestBase {
	private static final Logger logger = LoggerFactory.getLogger(VelocityWebTest.class);
	private static Document doc;
	
	@Before
	public void before() throws Exception {
		doc = Jsoup.connect(buildUrl("/testvm1.html")).timeout(60000).get();
	}
	
	@Test
	public void testCrsfMeta() throws Exception {
		boolean crsfMeta = false;
		Elements metas = doc.getElementsByTag("meta");
		for (Element meta : metas) {
			String tokenName = meta.attr("name");
			if (tokenName.equals("X-CSRF-TOKEN")) {
				String content = meta.attr("content");
				logger.info("crsf meta token={}", content);
				assertThat(content).isNotEmpty();
				crsfMeta = true;
			}
		}
		assertThat(crsfMeta).isTrue();
	}
	
	@Test
	public void testCrsfHidden() throws Exception {
		
		Element csrfInput = doc.select("input[name=_csrf]").first();
		assertThat(csrfInput).isNotNull();
		String crsfToken = csrfInput.attr("value");
		assertThat(crsfToken).isNotEmpty();
		logger.info("crsf input token={}", crsfToken);
		
	}
	
	@Test
	public void testToolbox() throws Exception {
		Element dateElement = doc.getElementsByTag("date").first();
		String dateVal = dateElement.text();
		logger.info("date format:{}", dateVal);
		Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateVal);
		assertThat(date).isNotNull();
		
		Element springUrl = doc.getElementsByTag("springUrl").first();
		String springUrlVal = springUrl.text();
		logger.info("spring toolbox :{}", springUrlVal);
		assertThat(springUrlVal).isNotNull();
		
	}
	
	@Test
	public void testLayout() throws Exception {
		Element title = doc.select("title").first();
		assertThat(title).isNotNull();
		String titleValue = title.html();
		assertThat(titleValue).isNotEmpty();
	}
	
	@Test
	public void testXss() throws Exception {
		Element xssElement = doc.getElementsByTag("xss").first();
		String xssVal = xssElement.html();
		logger.info("xss format:{}", xssVal);
		assertThat(xssVal).isEqualTo("&lt;script&gt;alert(111)&lt;/script&gt;");
	}
	
	@Test
	public void testEscape() throws Exception {
		HtmlEscapeReference htmlEscapeReference = new HtmlEscapeReference();
		assertThat(htmlEscapeReference.getRefName("$dateUtil")).startsWith("dateUtil");
		assertThat(htmlEscapeReference.getRefName("${dateUtil}")).startsWith("dateUtil");
		assertThat(htmlEscapeReference.getRefName("$!{dateUtil}")).startsWith("dateUtil");
		assertThat(htmlEscapeReference.getRefName("dateUtil")).startsWith("dateUtil");
	}
	
	/**
	 * 测试html escape此功能
	 * @throws Exception
	 */
	@Test
	public void testEscape1() throws Exception {
		HtmlEscapeReference htmlEscapeReference = new HtmlEscapeReference();
		String str = "<script>alert(111)</script>";
		assertThat(HtmlUtils.htmlEscape(str)).isEqualTo(htmlEscapeReference.referenceInsert("${df}", str));
		assertThat(str).isEqualTo(htmlEscapeReference.referenceInsert("${screen_content}", str));
	}
	
	/**
	 * 测试禁用html escape此功能
	 * @throws Exception
	 */
	@Test
	public void testEscape2() throws Exception {
		YijiVelocityProperties yijiVelocityProperties = ApplicationContextHolder.get().getBean(
			YijiVelocityProperties.class);
		boolean ori = yijiVelocityProperties.isHtmlEscapeEnable();
		yijiVelocityProperties.setHtmlEscapeEnable(false);
		HtmlEscapeReference htmlEscapeReference = new HtmlEscapeReference();
		String str = "<script>alert(111)</script>";
		assertThat(str).isEqualTo(htmlEscapeReference.referenceInsert("${df}", str));
		assertThat(str).isEqualTo(htmlEscapeReference.referenceInsert("${screen_content}", str));
		yijiVelocityProperties.setHtmlEscapeEnable(ori);
	}
	
	/**
	 * 测试html escape跳过某些ref
	 * @throws Exception
	 */
	@Test
	public void testEscape3() throws Exception {
		YijiVelocityProperties yijiVelocityProperties = ApplicationContextHolder.get().getBean(
			YijiVelocityProperties.class);
		yijiVelocityProperties.setRefSkipEscape("ah");
		String ori = yijiVelocityProperties.getRefSkipEscape();
		HtmlEscapeReference htmlEscapeReference = new HtmlEscapeReference();
		String str = "<script>alert(111)</script>";
		assertThat(str).isEqualTo(htmlEscapeReference.referenceInsert("${ah}", str));
		assertThat(str).isEqualTo(htmlEscapeReference.referenceInsert("${screen_content}", str));
		yijiVelocityProperties.setRefSkipEscape(ori);
	}
	
	/**
	 * 测试html escape跳过某些ref
	 * @throws Exception
	 */
	@Test
	public void testStringUtils() throws Exception {
		Element springUrl = doc.getElementsByTag("stringutil").first();
		String springUrlVal = springUrl.text();
		assertThat(springUrlVal).isEqualTo("185****9996");
	}
	
}
