/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-11-02 16:04 创建
 *
 */
package com.global.boot.tomcat;

import org.apache.coyote.AbstractProtocol;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.concurrent.Executor;

/**
 * @author qiubo@yiji.com
 */
public class TomcatMetrics implements PublicMetrics {
	
	private Optional<AbstractProtocol> optionalProtocol;
	
	public TomcatMetrics(AbstractProtocol abstractProtocol) {
		this.optionalProtocol = Optional.ofNullable(abstractProtocol);
	}
	
	@Override
	public Collection<Metric<?>> metrics() {
		Collection<Metric<?>> result = new LinkedHashSet<>();
		optionalProtocol.ifPresent(abstractProtocol -> {
			//当前连接数
			result.add(new Metric<>("tomcat.connectionCount", abstractProtocol.getConnectionCount()));
			result.add(new Metric<>("tomcat.maxThreads", abstractProtocol.getMaxThreads()));
			result.add(new Metric<>("tomcat.maxConnections", abstractProtocol.getMaxConnections()));
			result.add(new Metric<>("tomcat.minSpareThreads", abstractProtocol.getMinSpareThreads()));
			result.add(new Metric<>("tomcat.connectionTimeout", abstractProtocol.getConnectionTimeout()));
			Executor executor = abstractProtocol.getExecutor();
			if (executor instanceof ThreadPoolExecutor) {
				ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) executor;
				result.add(new Metric<>("tomcat.currentThreadCount", poolExecutor.getActiveCount()));
				result.add(new Metric<>("tomcat.submittedTaskCount", poolExecutor.getSubmittedCount()));
				result.add(new Metric<>("tomcat.completedTaskCount", poolExecutor.getCompletedTaskCount()));
			}
		});
		return result;
	}
}
