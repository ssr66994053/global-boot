/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-14 15:03 创建
 *
 */
package com.global.boot.core.log.initializer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.global.boot.core.Apps;
import com.global.boot.core.log.LogbackConfigurator;

/**
 * 添加info文件日志
 * @author qiubo@yiji.com
 */
public class FileInfoLogInitializer extends AbstractLogInitializer {
	@Override
	public void init(LogbackConfigurator configurator) {
		String fileName = Apps.getAppName() + "-info-30de.log";
		configurator.log("设置info级别的文件日志，日志文件为:%s", fileName);
		Appender<ILoggingEvent> appender = configurator.asyncFileAppender("FILE-INFO", configurator.getPattern(),
			fileName);
		configurator.root(Level.INFO, appender);
	}
}
