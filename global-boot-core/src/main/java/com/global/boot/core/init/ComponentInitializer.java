/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-09-10 10:43 创建
 *
 */
package com.yiji.boot.core.init;

import com.yiji.boot.core.Apps;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 组件初始化器，在applicationContext还没有初始化之前，提供给组件自定义的钩子
 * @author qiubo@yiji.com
 */
public interface ComponentInitializer extends AutoConfigExcluder {
	default void initialize(ConfigurableApplicationContext applicationContext) {
	}
	
	default void setPropertyIfMissing(String key, Object value) {
		if (!Apps.getEnvironment().containsProperty(key)) {
			System.setProperty(key, value.toString());
		}
	}
	
	default Boolean getBooleanDefaultTrue(String key) {
		return Apps.getEnvironment().getProperty(key, Boolean.class, Boolean.TRUE);
	}
	
	default Boolean getBooleanDefaultFalse(String key) {
		return Apps.getEnvironment().getProperty(key, Boolean.class, Boolean.FALSE);
	}
}
