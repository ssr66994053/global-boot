/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-11-01 10:51 创建
 */
package com.yiji.boot.core.listener;

import com.yiji.boot.core.Apps;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author qiubo@yiji.com
 */
public class AppHttpPortApplicationListener implements ApplicationListener<ApplicationPreparedEvent> {
	
	@Override
	public void onApplicationEvent(ApplicationPreparedEvent event) {
		new AppInfoWriter().write("app.httpport", Apps.getHttpPort());
	}
	
}