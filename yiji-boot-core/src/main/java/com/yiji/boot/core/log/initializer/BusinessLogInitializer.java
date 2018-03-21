/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-01-08 17:36 创建
 */
package com.yiji.boot.core.log.initializer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.yiji.boot.core.log.LogbackConfigurator;
import com.yjf.common.log.BusinessLogger;
import com.yjf.common.log.LogbackAsyncAppender;

/**
 * @author qiubo@yiji.com
 */
public class BusinessLogInitializer extends AbstractLogInitializer {
	@Override
	public void init(LogbackConfigurator configurator) {
		String fileName = "business/business-2dt.log";
		configurator.log("设置业务监控日志，日志文件为:%s", fileName);
		//创建异步file appender
		Appender<ILoggingEvent> appender = configurator.asyncFileAppender("BUSINESS-LOG", "%msg%n", fileName, 2);
		//异步日志不收集栈信息
		if (appender instanceof LogbackAsyncAppender) {
			((LogbackAsyncAppender) appender).setIncludeCallerData(false);
		}
		configurator.logger(BusinessLogger.class.getName(), Level.INFO, false, appender);
	}
}
