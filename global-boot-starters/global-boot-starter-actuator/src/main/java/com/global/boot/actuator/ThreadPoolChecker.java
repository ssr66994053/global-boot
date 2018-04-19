/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-11-04 13:51 创建
 */
package com.global.boot.actuator;

import com.global.boot.core.Apps;
import com.global.boot.core.listener.YijiApplicationRunListener;
import com.global.common.concurrent.MonitoredThreadPool;
import com.global.common.concurrent.MonitoredThreadPoolExecutor;
import com.global.common.env.Env;
import com.global.common.spring.ApplicationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author qiubo@yiji.com
 */
public class ThreadPoolChecker implements ApplicationListener<ApplicationReadyEvent> {
	private static final Logger logger = LoggerFactory.getLogger(ThreadPoolChecker.class);
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		if (Env.is(Env.sdev) || Env.is(Env.local)) {
			Map<String, ThreadPoolExecutor> threadPoolExecutorMap = ApplicationContextHolder.get().getBeansOfType(
				ThreadPoolExecutor.class);
			StringBuilder sb = new StringBuilder();
			boolean found = false;
            sb.append("\n************************************************************************\n");
			sb.append("为了保证apm系统中链路信息完整，我们检查了应用线程池的使用情况，如果没有使用yjf-common-util提供的线程池，程序启动失败：");
			for (Map.Entry<String, ThreadPoolExecutor> entry : threadPoolExecutorMap.entrySet()) {
				if (!(entry.getValue() instanceof MonitoredThreadPoolExecutor)) {
					found = true;
					sb.append("\nbeanName=").append(entry.getKey())
						.append("不是com.yjf.common.concurrent.MonitoredThreadPoolExecutor");
				}
			}
			
			Map<String, ThreadPoolTaskExecutor> threadPoolTaskExecutorMap = ApplicationContextHolder.get()
				.getBeansOfType(ThreadPoolTaskExecutor.class);
			for (Map.Entry<String, ThreadPoolTaskExecutor> entry : threadPoolTaskExecutorMap.entrySet()) {
				if (!(entry.getValue() instanceof MonitoredThreadPool)) {
					found = true;
					sb.append("\nbeanName=").append(entry.getKey())
						.append("不是com.yjf.common.concurrent.MonitoredThreadPool");
				}
			}
			if (found) {
                sb.append("\n************************************************************************");
                logger.warn("{}", sb.toString());
				Apps.shutdown();
			}
		}
	}
}
