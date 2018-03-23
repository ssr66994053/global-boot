/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-11-03 17:19 创建
 *
 */
package com.global.boot.core.log.initializer;

import ch.qos.logback.classic.Level;
import com.global.boot.core.log.LogbackConfigurator;

/**
 * @author qiubo@yiji.com
 */
public class CommonLogLevelSettingLogInitializer extends AbstractLogInitializer {
	@Override
	public void init(LogbackConfigurator configurator) {
		configurator.logger(
			"org.springframework.context.support.PostProcessorRegistrationDelegate$BeanPostProcessorChecker",
			Level.WARN);
		configurator.logger("org.apache.zookeeper.ZooKeeper", Level.WARN);
	}
}
