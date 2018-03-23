/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-08-05 17:42 创建
 */
package com.global.boot.actuator.health.check;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * 检查当前系统运行线程数
 * @author qiubo@yiji.com
 */
public class ThreadsCountHealthIndicator extends AbstractHealthIndicator {
	private static final int THREAD_COUNT_THRESHOLD = 2000;
	
	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		int threadCount = threadMXBean.getThreadCount();
		if (threadCount <= THREAD_COUNT_THRESHOLD) {
			builder.up();
		} else {
			builder.down();
		}
		builder.withDetail("threadCount", threadCount);
	}
}
