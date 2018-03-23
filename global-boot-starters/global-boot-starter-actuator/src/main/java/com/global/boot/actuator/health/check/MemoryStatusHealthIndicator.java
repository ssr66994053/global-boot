/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-10-29 21:30 创建
 *
 */
package com.yiji.boot.actuator.health.check;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

/**
 * @author qiubo@yiji.com
 */
public class MemoryStatusHealthIndicator extends AbstractHealthIndicator {
	
	private static final long avalibleMemthreshold = 5 * 1024 * 1024;//可用内存阈值=5M
	
	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		long freeMemory = runtime.freeMemory();
		long totalMemory = runtime.totalMemory();
		long maxMemory = runtime.maxMemory();
		long used = totalMemory - freeMemory;
		builder.withDetail("max", maxMemory / 1024 / 1024).withDetail("total", totalMemory / 1024 / 1024)
			.withDetail("used", used / 1024 / 1024).withDetail("free", freeMemory / 1024 / 1024);
		boolean ok = maxMemory - used > avalibleMemthreshold; // 剩余空间小于时报警
		if (ok) {
			builder.up();
		} else {
			builder.down();
		}
	}
	
}
