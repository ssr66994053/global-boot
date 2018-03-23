/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-09-23 23:32 创建
 *
 */
package com.yiji.boot.web;

import com.yiji.boot.web.common.EnvServlet;
import com.yiji.boot.web.common.ResponseHeaderFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * @author qiubo@yiji.com
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties({ ResponseHeaderProperties.class })
public class CommonWebAutoConfiguration {
	/**
	 * 提供给前端获取环境
	 */
	@Bean
	public ServletRegistrationBean envServlet() {
		EnvServlet envServlet = new EnvServlet();
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(envServlet, "/boot/env");
		servletRegistrationBean.setName("bootEnvServlet");
		return servletRegistrationBean;
	}
	
	@Bean
	public Filter responseHeaderFilter(ResponseHeaderProperties responseHeaderProperties,
										YijiWebProperties yijiWebProperties) {
		ResponseHeaderFilter responseHeaderFilter = new ResponseHeaderFilter(responseHeaderProperties,
			yijiWebProperties.getCacheMaxAge());
		return responseHeaderFilter;
	}
}
