/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-05-17 19:50 创建
 */
package com.global.boot.dubbo;

import com.global.boot.core.AppConfigException;
import com.global.common.dependency.DependencyChecker;
import org.springframework.core.env.Environment;

import static com.global.common.dependency.DependencyChecker.Utils.isPortUsing;

/**
 * @author qiubo@yiji.com
 */
public class DubboDependencyChecker implements DependencyChecker {
	@Override
	public void check(Environment environment) {
		if (environment.getProperty("yiji.dubbo.enable", Boolean.class, Boolean.TRUE)) {
			if (environment.getProperty("yiji.dubbo.provider.enable", Boolean.class, Boolean.TRUE)) {
				Integer port = environment.getRequiredProperty("yiji.dubbo.provider.port", Integer.class);
				if (isPortUsing(port.intValue())) {
					throw new AppConfigException("dubbo port:" + port + " is using.");
				}
			}
		}
	}
	
}
