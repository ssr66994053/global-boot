/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-08-24 17:01 创建
 */
package com.global.boot.cas;

import com.global.boot.core.Apps;
import com.global.boot.core.init.ComponentInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ClassUtils;

/**
 * @author qiubo@yiji.com
 */
public class CASComponentInitializer implements ComponentInitializer {
	private static final Logger logger = LoggerFactory.getLogger(CASComponentInitializer.class);
	
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		String enable = applicationContext.getEnvironment().getProperty("yiji.cas.enable");
		if (enable == null || Boolean.valueOf(enable)) {
			if (ClassUtils.isPresent("com.yiji.boot.shiro.ShiroAutoConfiguration", applicationContext.getClassLoader())) {
				String shiroEnable = applicationContext.getEnvironment().getProperty("yiji.shiro.enable");
				if (shiroEnable == null || Boolean.valueOf(shiroEnable)) {
					logger.warn("由于shiro和cas组件冲突。当启用cas组件时排除掉shiro组件依赖或者配置yiji.shiro.enable=false。cas组件完全可以满足shiro组件的能力。");
					Apps.shutdown();
				}
			}
		}
	}
}
