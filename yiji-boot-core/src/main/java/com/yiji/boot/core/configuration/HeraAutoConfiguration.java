/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-06 15:45 创建
 *
 */
package com.yiji.boot.core.configuration;

import com.yiji.boot.core.Apps;
import com.yiji.boot.core.hera.HeraConfigPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiubo@yiji.com
 */
@Configuration
@ConditionalOnProperty(value = Apps.HERA_ENABLE, matchIfMissing = true)
public class HeraAutoConfiguration {
	@Bean
	public static BeanPostProcessor addHeraBeanFactoryPostProcessor() {
		return new HeraConfigPostProcessor();
	}
}
