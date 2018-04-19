/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-10 11:12 创建
 *
 */
package com.global.boot.xss;

import javax.servlet.Filter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.global.common.web.CrossScriptingFilter;

/**
 * @author qiubo@yiji.com
 */
@Configuration
@ConditionalOnProperty(value = "yiji.xss.enable", matchIfMissing = true)
@ConditionalOnWebApplication
@EnableConfigurationProperties({ XSSProperties.class })
public class XSSAutoConfiguration {
	@Autowired
	protected XSSProperties xssProperties;
	
	@Bean
	public Filter xssFilter() {
		CrossScriptingFilter crossScriptingFilter = new CrossScriptingFilter();

		//忽略boss系统的路径，将boss相关的交给boss-starter处理
		String excludeUrlPath = StringUtils.isBlank(xssProperties.getExcludeUrlPath()) ? "/boss/"
			: xssProperties.getExcludeUrlPath() + ",/boss/";
		xssProperties.setExcludeUrlPath(excludeUrlPath);
		crossScriptingFilter.setXssConfig(xssProperties);

		return crossScriptingFilter;
	}
	
}
