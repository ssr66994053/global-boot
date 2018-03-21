/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-04-13 11:33 创建
 *
 */
package com.yiji.boot.actuator.health.guard;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;

/**
 * @author daidai@yiji.com
 */
public class HealthGuardStartingListener implements ApplicationListener<ApplicationReadyEvent>, ApplicationContextAware {
	private HealthGuard healthGuard;
	private ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		this.healthGuard = applicationContext.getBean("healthGuard", HealthGuard.class);
		this.healthGuard.start();
	}
}
