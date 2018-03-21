/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-09 20:10 创建
 */
package com.yiji.boot.test.threadpool;

import com.alibaba.dubbo.common.utils.NamedThreadFactory;
import com.yjf.common.concurrent.MonitoredThreadPoolExecutor;
import com.yjf.common.concurrent.ReporttingRejectedExecutionHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author qiubo@yiji.com
 */
@Slf4j
public class ThreadPoolTest {
	private MonitoredThreadPoolExecutor executor;
	private ExecutorService executorService;
	
	@Before
	public void setUp() throws Exception {
	    System.setProperty("yiji.appName","TEST");
        System.setProperty("spring.profiles.active","TEST");
		ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue(5);
		String name = "test";
		executor = new MonitoredThreadPoolExecutor(2, 5, 10, TimeUnit.MINUTES, arrayBlockingQueue,
			new NamedThreadFactory(name, true), new ReporttingRejectedExecutionHandler(name));
        executor.initialize();
		executorService = Executors.newFixedThreadPool(200);
	}
	
	@Test
	public void name() throws Exception {
        AtomicInteger idx=new AtomicInteger(0);
		for (int i = 0; i < 10; i++) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					executor.submit(() -> {
                        try {
                            Thread.sleep(1000);
                           log.info("{}",idx.getAndIncrement());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
				}
			});
		}
		Thread.sleep(100000);
	}
}