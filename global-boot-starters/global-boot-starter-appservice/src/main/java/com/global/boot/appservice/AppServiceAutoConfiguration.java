/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-09-22 16:02 创建
 */
package com.global.boot.appservice;

import com.global.boot.core.Apps;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiubo@yiji.com
 */
@Configuration
@EnableConfigurationProperties({ AppServiceProperties.class })
public class AppServiceAutoConfiguration {
	
	@Bean
	public static AppServiceBeanPostProcessor facadeBeanPostProcessor(AppServiceProperties appServiceProperties) {
		Apps.buildProperties(appServiceProperties);
		appServiceProperties.check();
		return new AppServiceBeanPostProcessor(appServiceProperties);
	}
	
}
