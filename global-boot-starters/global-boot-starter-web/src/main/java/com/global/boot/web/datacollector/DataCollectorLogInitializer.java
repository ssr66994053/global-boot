/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-11-17 14:44 创建
 *
 */
package com.yiji.boot.web.datacollector;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.yiji.boot.core.Apps;
import com.yiji.boot.core.log.LogbackConfigurator;
import com.yiji.boot.core.log.initializer.AbstractLogInitializer;
import com.yjf.common.log.LogbackAsyncAppender;

/**
 * @author yanglie@yiji.com
 */
public class DataCollectorLogInitializer extends AbstractLogInitializer {
	@Override
	public void init(LogbackConfigurator configurator) {
		//此组件启用时才添加日志配置
		if (configurator.getEnvironment().getProperty("yiji.dataCollector.enable", Boolean.class, Boolean.FALSE)) {
			String fileName = Apps.getAppName() + "-data-collector-30de.log";
			configurator.log("设置数据收集文件，日志文件为:%s", fileName);
			//创建异步file appender
			Appender<ILoggingEvent> appender = configurator.asyncFileAppender("DATA-COLLECTOR",
				"%d{yyyy-MM-dd HH:mm:ss.SSS} %msg%n", fileName);
			//异步日志不收集栈信息
			if (appender instanceof LogbackAsyncAppender) {
				((LogbackAsyncAppender) appender).setIncludeCallerData(false);
			}
			configurator.logger(DataCollectorLogger.class.getName(), Level.INFO, false, appender);
		}
	}
}
