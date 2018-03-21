/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-08-24 17:18 创建
 */
package com.yiji.boot.core.listener;

import com.yjf.common.spring.ApplicationContextHolder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

/**
 * @author qiubo@yiji.com
 */
public class HolderInitApplicationContextInitializer implements
													ApplicationContextInitializer<ConfigurableApplicationContext>,
													PriorityOrdered {
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		new ApplicationContextHolder().setApplicationContext(applicationContext);
	}
	
	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
}
