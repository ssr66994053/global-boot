/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-15 12:32 创建
 *
 */
package com.global.boot.jdbc;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.global.boot.core.log.LogbackConfigurator;
import com.global.boot.core.log.initializer.AbstractLogInitializer;
import com.yjf.common.log.LogbackAsyncAppender;

/**
 * 打印慢sql和big result sql到sql-10dt.log文件
 * @author qiubo@yiji.com
 */
public class DruidLogInitializer extends AbstractLogInitializer {
	@Override
	public void init(LogbackConfigurator configurator) {
		//此组件启用时才添加日志配置
		if (configurator.getEnvironment().getProperty("yiji.ds.enable", Boolean.class, Boolean.TRUE)) {
			String fileName = "sql-2dt.log";
			configurator.log("设置数据库访问性能日志，日志文件为:%s", fileName);
			//创建异步file appender
			Appender<ILoggingEvent> appender = configurator.asyncFileAppender("DRUID-SQL", "%msg%n", fileName, 2);
			//异步日志不收集栈信息
			if (appender instanceof LogbackAsyncAppender) {
				((LogbackAsyncAppender) appender).setIncludeCallerData(false);
			}
			configurator.logger("com.yiji.common.ds.sql", Level.INFO, false, appender);
		}
	}
}
