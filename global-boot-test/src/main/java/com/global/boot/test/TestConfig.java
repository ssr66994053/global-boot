/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-10 17:59 创建
 *
 */
package com.global.boot.test;

//import com.global.framework.hera.client.support.annotation.AutoConfig;
import com.yjf.common.concurrent.MonitoredThreadPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

/**
 * @author qiubo@yiji.com
 */
@Configuration
public class TestConfig {
	@Value("${app.test}")
	private String valueFormHera;
	
//	@AutoConfig("app.test1")
//	private String valueFormHera1;
	
	@Value("${xxxxx}")
	private String xxxxx;
	
	@Bean
	public TestBean1 testBean1() {
		TestBean1 testBean1 = new TestBean1();
		testBean1.setName(",xx");
		testBean1.setXxxx(xxxxx);
		return testBean1;
	}
	
	@Bean
	public ServletRegistrationBean testServlet(MultipartConfigElement multipartConfigElement) {
		TestServlet envServlet = new TestServlet();
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(envServlet, "/testServlet");
		servletRegistrationBean.setName("testServlet");
		servletRegistrationBean.setMultipartConfig(multipartConfigElement);
		return servletRegistrationBean;
	}
	
	@Bean
	public MonitoredThreadPool monitoredThreadPool() {
		MonitoredThreadPool monitoredThreadPool = new MonitoredThreadPool();
		return monitoredThreadPool;
	}
	
//	public String getValueFormHera1() {
//		return valueFormHera1;
//	}
//	
//	public void setValueFormHera1(String valueFormHera1) {
//		this.valueFormHera1 = valueFormHera1;
//	}
	
	public String getValueFormHera() {
		return valueFormHera;
	}
	
	public void setValueFormHera(String valueFormHera) {
		this.valueFormHera = valueFormHera;
	}
	
	public String getXxxxx() {
		return xxxxx;
	}
	
	public void setXxxxx(String xxxxx) {
		this.xxxxx = xxxxx;
	}
}
