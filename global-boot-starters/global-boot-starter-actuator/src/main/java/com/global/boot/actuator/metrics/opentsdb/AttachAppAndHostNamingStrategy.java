/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-04-11 14:34 创建
 *
 */
package com.global.boot.actuator.metrics.opentsdb;

import com.global.boot.core.Apps;
import com.global.common.lang.ip.IPUtil;
import org.springframework.boot.actuate.metrics.opentsdb.OpenTsdbName;
import org.springframework.boot.actuate.metrics.opentsdb.OpenTsdbNamingStrategy;

/**
 * @author daidai@yiji.com
 */
public class AttachAppAndHostNamingStrategy implements OpenTsdbNamingStrategy {
	private OpenTsdbNamingStrategy wrapped;
	
	public AttachAppAndHostNamingStrategy(OpenTsdbNamingStrategy wrapped) {
		this.wrapped = wrapped;
	}
	
	@Override
	public OpenTsdbName getName(String metricName) {
		final OpenTsdbName name = wrapped.getName(metricName);
		if (name.getTags().get("appName") == null) {
			name.getTags().put("appName", Apps.getAppName());
		}
		
		if (name.getTags().get("host") == null) {
			name.getTags().put("host", IPUtil.getFirstNoLoopbackIPV4Address());
		}
		
		return name;
	}
}
