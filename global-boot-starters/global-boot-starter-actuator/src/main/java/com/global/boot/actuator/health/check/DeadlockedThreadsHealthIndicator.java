/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-10-29 21:17 创建
 *
 */
package com.global.boot.actuator.health.check;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author qiubo@yiji.com
 */
public class DeadlockedThreadsHealthIndicator extends AbstractHealthIndicator {
	private ThreadDeadlockDetector detector = new ThreadDeadlockDetector();
	
	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		final Set<String> threads = detector.getDeadlockedThreads();
		if (threads.isEmpty()) {
			builder.up();
		} else {
			builder.down().withDetail("deadlockedThreads", threads.toString());
		}
	}
	
	private static class ThreadDeadlockDetector {
		private static final int MAX_STACK_TRACE_DEPTH = 100;
		
		private final ThreadMXBean threads;
		
		/**
		 * Creates a new detector.
		 */
		public ThreadDeadlockDetector() {
			this(ManagementFactory.getThreadMXBean());
		}
		
		/**
		 * Creates a new detector using the given {@link ThreadMXBean}.
		 *
		 * @param threads a {@link ThreadMXBean}
		 */
		public ThreadDeadlockDetector(ThreadMXBean threads) {
			this.threads = threads;
		}
		
		/**
		 * Returns a set of diagnostic stack traces for any deadlocked threads. If no threads are deadlocked, returns an
		 * empty set.
		 *
		 * @return stack traces for deadlocked threads or an empty set
		 */
		public Set<String> getDeadlockedThreads() {
			final long[] ids = threads.findDeadlockedThreads();
			if (ids != null) {
				final Set<String> deadlocks = new HashSet<String>();
				for (ThreadInfo info : threads.getThreadInfo(ids, MAX_STACK_TRACE_DEPTH)) {
					final StringBuilder stackTrace = new StringBuilder();
					for (StackTraceElement element : info.getStackTrace()) {
						stackTrace.append("\t at ").append(element.toString()).append(String.format("%n"));
					}
					
					deadlocks.add(String.format("%s locked on %s (owned by %s):%n%s", info.getThreadName(),
						info.getLockName(), info.getLockOwnerName(), stackTrace.toString()));
				}
				return Collections.unmodifiableSet(deadlocks);
			}
			return Collections.emptySet();
		}
	}
	
}
