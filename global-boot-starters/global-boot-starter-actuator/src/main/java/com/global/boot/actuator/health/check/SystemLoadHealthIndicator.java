/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-10-29 21:25 创建
 *
 */
package com.global.boot.actuator.health.check;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 * @author qiubo@yiji.com
 */
public class SystemLoadHealthIndicator extends AbstractHealthIndicator {
	
	private static final int FACTOR = 3;
	
	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		double load = operatingSystemMXBean.getSystemLoadAverage();
		int cpu = operatingSystemMXBean.getAvailableProcessors();
		builder.withDetail("load", load).withDetail("cpu", cpu);
		if (load <= cpu * FACTOR) {
			builder.up();
		} else {
			builder.down();
		}
	}
}
