/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-03-01 16:01 创建
 */
package com.global.boot.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.threadpool.ThreadPool;
import com.yjf.common.concurrent.MonitoredThreadPool;
import com.yjf.common.concurrent.MonitoredThreadPoolExecutor;
import com.yjf.common.lang.exception.Exceptions;

import java.util.concurrent.Executor;

/**
 * @author qiubo@yiji.com
 */
public class YijiDubboThreadPool implements ThreadPool {

	/**
	 * 此类也是单例的,所以这里就用静态方法了,dubbo提供的机制实在太恶心,将就下
	 */
	static MonitoredThreadPoolExecutor threadPoolExecutor;

	@Override
	public Executor getExecutor(URL url) {
		//使用固定线程池
		String name = url.getParameter(Constants.THREAD_NAME_KEY, Constants.DEFAULT_THREAD_NAME);
		int cores = url.getParameter(Constants.CORE_THREADS_KEY, Constants.DEFAULT_CORE_THREADS);
		int threads = url.getParameter(Constants.THREADS_KEY, Integer.MAX_VALUE);
		int queues = url.getParameter(Constants.QUEUES_KEY, Constants.DEFAULT_QUEUES);
		int alive = url.getParameter(Constants.ALIVE_KEY, Constants.DEFAULT_ALIVE);
		MonitoredThreadPool monitoredThreadPool = new MonitoredThreadPool();
		monitoredThreadPool.setThreadNamePrefix(name + "-");
		monitoredThreadPool.setCorePoolSize(cores);
		monitoredThreadPool.setMaxPoolSize(threads);
		monitoredThreadPool.setQueueCapacity(queues);
		monitoredThreadPool.setKeepAliveSeconds(alive);
		monitoredThreadPool.setAllowCoreThreadTimeOut(false);

		monitoredThreadPool.setMetrics(getMetricName(name));
		monitoredThreadPool.afterPropertiesSet();
		try {
			threadPoolExecutor = (MonitoredThreadPoolExecutor) monitoredThreadPool.getThreadPoolExecutor();
		} catch (IllegalStateException e) {
			throw Exceptions.newRuntimeExceptionWithoutStackTrace(e);
		}
		setThreadPoolExecutor(threadPoolExecutor);
		return threadPoolExecutor;
	}

	private String getMetricName(String threadName) {
		int idx = threadName.indexOf('-');
		if (idx != -1) {
			return threadName.substring(0, idx);
		}else {
			return threadName;
		}
	}
	
	private static void setThreadPoolExecutor(MonitoredThreadPoolExecutor threadPoolExecutor) {
		YijiDubboThreadPool.threadPoolExecutor = threadPoolExecutor;
	}
	
}
