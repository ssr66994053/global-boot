/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-01-12 17:08 创建
 */
package com.yiji.boot.boss;

import ch.qos.logback.classic.Level;
import com.yiji.boot.core.log.LogbackConfigurator;
import com.yiji.boot.core.log.initializer.AbstractLogInitializer;

/**
 * @author qiubo@yiji.com
 */
public class CASLogInitializer extends AbstractLogInitializer {
	@Override
	public void init(LogbackConfigurator configurator) {
		if (configurator.getEnvironment().getProperty("yiji.boss.enable", Boolean.class, Boolean.TRUE)) {
			configurator.logger("com.yjf.marmot", Level.WARN);
		}
	}
}
