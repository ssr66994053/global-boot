/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-09 21:48 创建
 */
package com.yiji.boot.test.threadpool;

import com.yjf.common.concurrent.MonitoredThreadPoolExecutor;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author qiubo@yiji.com
 */
public class MonitoredThreadPoolQueue extends LinkedBlockingQueue<Runnable> {
	private volatile MonitoredThreadPoolExecutor parent = null;
	
	public MonitoredThreadPoolQueue(int capacity) {
		super(capacity);
	}
	
	public void setParent(MonitoredThreadPoolExecutor tp) {
		parent = tp;
	}
	
	@Override
	public boolean offer(Runnable o) {
		//we can't do any checks
		if (parent == null)
			return super.offer(o);
		//we are maxed out on threads, simply queue the object
		if (parent.getPoolSize() == parent.getMaximumPoolSize())
			return super.offer(o);
		//we have idle threads, just add it to the queue
		if (parent.getSubmittedCount() < (parent.getPoolSize()))
			return super.offer(o);
		//if we have less threads than maximum force creation of a new thread
		if (parent.getPoolSize() < parent.getMaximumPoolSize())
			return false;
		//if we reached here, we need to add it to the queue
		return super.offer(o);
	}
	
}
