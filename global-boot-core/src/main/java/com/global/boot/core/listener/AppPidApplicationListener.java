/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-25 21:19 创建
 *
 */
package com.yiji.boot.core.listener;

import com.yiji.boot.core.Apps;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;

/**
 * 写入pid到文件系统
 * @author qiubo@yiji.com
 */
public class AppPidApplicationListener implements ApplicationListener<ApplicationPreparedEvent> {
	
	@Override
	public void onApplicationEvent(ApplicationPreparedEvent event) {
		new AppInfoWriter().write("app.pid", Apps.getPid());
	}
}
