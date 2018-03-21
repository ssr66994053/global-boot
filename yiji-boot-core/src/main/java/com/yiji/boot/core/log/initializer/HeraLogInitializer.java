/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-05-06 15:32 创建
 */
package com.yiji.boot.core.log.initializer;

import ch.qos.logback.classic.Level;
import com.yiji.boot.core.log.LogbackConfigurator;

/**
 * @author qiubo@yiji.com
 */
public class HeraLogInitializer extends AbstractLogInitializer {
	@Override
	public void init(LogbackConfigurator configurator) {
		configurator.logger("com.yiji.framework.hera.client", Level.WARN);
	}
}
