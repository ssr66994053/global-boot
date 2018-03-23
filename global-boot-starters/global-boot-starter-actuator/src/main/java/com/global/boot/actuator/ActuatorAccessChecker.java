/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-06-03 14:21 创建
 */
package com.global.boot.actuator;

import com.alibaba.fastjson.JSON;
import com.github.kevinsawicki.http.HttpRequest;
import com.global.boot.core.Apps;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author qiubo@yiji.com
 */
public class ActuatorAccessChecker implements ApplicationListener<ApplicationReadyEvent> {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ActuatorAccessChecker.class);
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		String body = HttpRequest.get("http://127.0.0.1:" + Apps.getHttpPort() + "/mgt/info").body();
		try {
			JSON.parse(body);
		} catch (Exception e) {
			logger.error("应用/mgt/*路径访问异常,可能被应用配置的filter阻止了,请检查应用保证/mgt/*可以被正常访问");
			Apps.shutdown();
		}
		
	}
}
