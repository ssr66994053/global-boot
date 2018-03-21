/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-09-10 10:51 创建
 *
 */
package com.yiji.boot.velocity;

import com.yiji.boot.core.init.ComponentInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author qiubo@yiji.com
 */
public class VelocityComponentInitializer implements ComponentInitializer {
	@Override
	public void initialize(ConfigurableApplicationContext context) {
		//fix spring boot bug velocity enable don't work
		if (!Boolean.valueOf(context.getEnvironment().getProperty("spring.velocity.enabled", "true"))) {
			System.setProperty("spring.velocity.checkTemplateLocation", Boolean.FALSE.toString());
			System.setProperty("yiji.velocity.enable", Boolean.FALSE.toString());
		}
		if (!Boolean.valueOf(context.getEnvironment().getProperty("yiji.velocity.enable", "true"))) {
			System.setProperty("spring.velocity.checkTemplateLocation", Boolean.FALSE.toString());
			System.setProperty("spring.velocity.enabled", Boolean.FALSE.toString());
		}
	}
}
