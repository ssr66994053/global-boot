/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-01-16 08:47 创建
 *
 */
package com.yiji.boot.amqp;

import com.google.common.collect.Lists;
import com.yiji.boot.core.init.ComponentInitializer;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

/**
 * @author yanglie@yiji.com
 */
public class RabbitComponentInitializer implements ComponentInitializer {
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		System.setProperty("management.health.rabbit.enabled", Boolean.FALSE.toString());
	}
	
	@Override
	public List<String> excludeAutoconfigClassNames() {
		return Lists.newArrayList(RabbitAutoConfiguration.class.getName());
	}
}
