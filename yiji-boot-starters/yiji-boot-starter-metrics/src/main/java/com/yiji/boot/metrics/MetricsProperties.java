/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2015-09-15 16:14 创建
 *
 */
package com.yiji.boot.metrics;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author daidai@yiji.com
 */
@ConfigurationProperties(MetricsProperties.PREFIX)
public class MetricsProperties {
	public static final String PREFIX = "yiji.metrics";
	
	/**
	 * 是否启用JmxReporter。如果不想通过Jmx暴露metrics信息，可以关闭。
	 */
	private boolean enableJmxReporter = true;
	
	public boolean isEnableJmxReporter() {
		return enableJmxReporter;
	}
	
	public void setEnableJmxReporter(boolean enableJmxReporter) {
		this.enableJmxReporter = enableJmxReporter;
	}
}
