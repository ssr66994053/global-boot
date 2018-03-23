/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-12 22:01 创建
 *
 */
package com.global.boot.core.log.initializer;

import com.global.boot.core.log.LogbackConfigurator;
import org.springframework.core.annotation.Order;

/**
 * log初始化扩展
 * @author qiubo@yiji.com
 */
@Order(0)
public interface LogInitializer {
	void init(LogbackConfigurator configurator);
}
