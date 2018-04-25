/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-03-28 17:11 创建
 */
package com.global.boot.postman;

import com.global.common.concurrent.MonitoredThreadPool;
import com.global.common.net.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

import javax.annotation.Resource;
import java.net.HttpURLConnection;

/**
 * @author qiubo@yiji.com
 */
public class PostmanHealthIndicator extends AbstractHealthIndicator {
	
	@Resource(name = "postmanThreadPool")
	private MonitoredThreadPool monitoredThreadPool;
	@Autowired
	private PostmanProperties postmanProperties;
	
	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		String url = postmanProperties.getHealthCheckUrl();
		try {
			int code = HttpRequest.head(url).connectTimeout(10000).readTimeout(5000).trustAllCerts().trustAllHosts()
				.code();
			if (code != HttpURLConnection.HTTP_OK) {
				builder.down().withDetail("responseCode", code).withDetail("url", url);
				return;
			}
		} catch (Exception e) {
			builder.withDetail("url", url);
			builder.down(e);
			return;
		}
		int current = monitoredThreadPool.getActiveCount();
		int max = monitoredThreadPool.getMaxPoolSize();
		int remainingCapacity = monitoredThreadPool.getThreadPoolExecutor().getQueue().remainingCapacity();
		if (current == max) {
			if (remainingCapacity < 1) {
				builder.down().withDetail("remainingCapacity", remainingCapacity);
			}
		} else {
			builder.up().withDetail("activeCount", current).withDetail("remainingCapacity", remainingCapacity);
		}
	}
}
