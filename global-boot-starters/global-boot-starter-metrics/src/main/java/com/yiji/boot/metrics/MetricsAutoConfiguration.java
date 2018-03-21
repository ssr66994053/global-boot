/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2015-09-16 16:17 创建
 *
 */
package com.yiji.boot.metrics;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import com.yjf.common.metrics.MetricsHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author daidai@yiji.com
 */
@Configuration
@EnableMetrics
@EnableConfigurationProperties({ MetricsProperties.class })
public class MetricsAutoConfiguration extends MetricsConfigurerAdapter {
	@Autowired
	private MetricsProperties metricsProperties;
	
	@Override
	public void configureReporters(MetricRegistry metricRegistry) {
		if (metricsProperties.isEnableJmxReporter()) {
			registerReporter(JmxReporter.forRegistry(metricRegistry).build()).start();
		}
	}
	
	@Override
	public MetricRegistry getMetricRegistry() {
		return MetricsHolder.metricRegistry();
	}
	
	@Override
	public HealthCheckRegistry getHealthCheckRegistry() {
		return MetricsHolder.healthCheckRegistry();
	}
}
