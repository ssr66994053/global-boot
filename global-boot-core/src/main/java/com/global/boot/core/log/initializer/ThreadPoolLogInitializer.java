/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-01-08 17:36 创建
 */
package com.global.boot.core.log.initializer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.global.boot.core.log.LogbackConfigurator;
import com.yjf.common.log.BusinessLogger;

/**
 * @author qiubo@yiji.com
 */
public class ThreadPoolLogInitializer extends AbstractLogInitializer {
	@Override
	public void init(LogbackConfigurator configurator) {
		String fileName = "threadpool-2dt.log";
		configurator.log("设置线程池监控日志，日志文件为:%s", fileName);
		Appender<ILoggingEvent> appender = configurator.fileAppender("THREADPOOL_LOG", "%msg%n", fileName, 2);
		//com.yjf.common.concurrent.ReporttingRejectedExecutionHandler
		configurator.logger("THREADPOOL_LOG", Level.INFO, false, appender);
	}
}
