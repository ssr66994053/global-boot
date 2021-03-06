/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-09-10 11:23 创建
 *
 */
package com.global.boot.dubbo;

import com.global.boot.core.init.ComponentInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author qiubo@yiji.com
 */
public class DubboComponentInitializer implements ComponentInitializer {
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		//tuning dubbo logger ,avoid log4j initialize
		System.setProperty("dubbo.application.logger", "slf4j");
	}
}
