/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-04-06 10:13 创建
 *
 */
package com.yiji.boot.core.metrics;

import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;

import javax.management.ObjectName;
import java.util.Collection;

/**
 * @author daidai@yiji.com
 */
public interface JmxMetrics //extends PublicMetrics
{
	
	ObjectName getObjectName();
	
	String displayPrefix();
	
	Collection<String> getAttributeKeys();

	Collection<Metric<?>> metrics();
}
