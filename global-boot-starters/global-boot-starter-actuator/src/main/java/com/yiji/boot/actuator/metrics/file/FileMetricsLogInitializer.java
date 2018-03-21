/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-02-23 10:42 创建
 */
package com.yiji.boot.actuator.metrics.file;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.yiji.boot.core.log.LogbackConfigurator;
import com.yiji.boot.core.log.initializer.AbstractLogInitializer;

import static com.yiji.boot.actuator.metrics.file.MetricsFileWriter.METRICS_LOG;

/**
 * @author qiubo@yiji.com
 */
public class FileMetricsLogInitializer extends AbstractLogInitializer {
	@Override
	public void init(LogbackConfigurator configurator) {
		String fileName = "metrics-2dt.log";
		configurator.log("设置metrics日志，日志文件为:%s", fileName);
		Appender<ILoggingEvent> appender = configurator.fileAppender(METRICS_LOG, "%msg%n", fileName, 2);
		configurator.logger(METRICS_LOG, Level.INFO, false, appender);
	}
}
