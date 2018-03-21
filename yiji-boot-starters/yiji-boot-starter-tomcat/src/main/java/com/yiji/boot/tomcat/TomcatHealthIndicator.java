/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-03-28 16:43 创建
 */
package com.yiji.boot.tomcat;

import org.apache.coyote.AbstractProtocol;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

import java.util.Optional;
import java.util.concurrent.Executor;

/**
 * @author qiubo@yiji.com
 */
public class TomcatHealthIndicator extends AbstractHealthIndicator {
	private Optional<AbstractProtocol> optionalProtocol;
	private TomcatProperties tomcatProperties;
	
	public TomcatHealthIndicator(AbstractProtocol abstractProtocol, TomcatProperties tomcatProperties) {
		this.optionalProtocol = Optional.ofNullable(abstractProtocol);
		this.tomcatProperties = tomcatProperties;
	}
	
	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		builder.up();
		this.optionalProtocol.ifPresent(abstractProtocol -> {
			Executor executor = abstractProtocol.getExecutor();
			if (executor instanceof ThreadPoolExecutor) {
				ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) executor;
				int submittedCount = poolExecutor.getSubmittedCount();
				int maxThread = tomcatProperties.getMaxThreads();
				builder.withDetail("maxThreads", maxThread).withDetail("submittedCount", submittedCount);
				
				//如果队列中任务数大于最大线程数的一半
			if (submittedCount - maxThread >= maxThread / 2) {
				builder.down();
			}
		}
	})	;
	}
}
