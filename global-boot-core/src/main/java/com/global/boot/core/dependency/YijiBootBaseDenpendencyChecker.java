/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-05-17 19:41 创建
 */
package com.yiji.boot.core.dependency;

import com.yiji.boot.core.AppConfigException;
import com.yiji.boot.core.Apps;
import com.yjf.common.dependency.DependencyChecker;
import org.springframework.core.env.Environment;

import java.io.File;

import static com.yjf.common.dependency.DependencyChecker.Utils.isPortUsing;

/**
 * @author qiubo@yiji.com
 */
public class YijiBootBaseDenpendencyChecker implements DependencyChecker {
	@Override
	public void check(Environment environment) {
		checkFiles();
		if (isPortUsing(Apps.getHttpPort())) {
			throw new AppConfigException("http port:" + Apps.getHttpPort() + " is using.");
		}
	}
	
	private void checkFiles() {
		String logPath = Apps.getLogPath();
		File logPathFile = new File(logPath);
		if (!logPathFile.exists() && !logPathFile.mkdirs()) {
			throw new AppConfigException("日志目录" + logPath + "创建失败.");
		}
		File appDataPath = new File(Apps.getAppDataPath());
		if (!appDataPath.exists() && !appDataPath.mkdirs()) {
			throw new AppConfigException("应用数据目录" + Apps.getAppDataPath() + "创建失败.");
		}
	}
	
}
