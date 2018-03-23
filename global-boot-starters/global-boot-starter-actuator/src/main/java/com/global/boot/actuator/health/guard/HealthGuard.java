/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-04-12 09:38 创建
 *
 */
package com.yiji.boot.actuator.health.guard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kevinsawicki.http.HttpRequest;
import com.yiji.boot.core.Apps;
import com.yiji.boot.core.Versions;
import com.yiji.framework.hera.client.support.annotation.AutoConfig;
import com.yjf.common.env.Env;
import com.yjf.common.lang.ip.IPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author daidai@yiji.com
 */

public class HealthGuard implements ApplicationListener<ContextClosedEvent> {
	private static final Logger logger = LoggerFactory.getLogger(HealthGuard.class);
	
	private static final int DEFAULT_CHECK_INTERVAL_IN_SECS = 30;
	@Autowired
	private Map<String, HealthIndicator> healthIndicators;
	private ScheduledExecutorService scheduledExecutorService;
	private ObjectMapper objectMapper;
	@AutoConfig("yiji.healthGuard.notifyAddress")
	private volatile String notifyAddress;
	@AutoConfig("yiji.healthGuard.notifyEnable")
	private volatile boolean notifyEnable;
	private int checkInterval;
	private String uptime = null;
	private volatile boolean stop = false;
	
	public HealthGuard() {
		this("");
	}
	
	public HealthGuard(String notifyAddress) {
		this(notifyAddress, DEFAULT_CHECK_INTERVAL_IN_SECS);
	}
	
	public HealthGuard(String notifyAddress, int checkInterval) {
		uptime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(ManagementFactory.getRuntimeMXBean()
			.getStartTime()));
		this.notifyAddress = notifyAddress;
		this.checkInterval = checkInterval;
		this.objectMapper = new ObjectMapper();
		
		this.scheduledExecutorService = Executors.newScheduledThreadPool(1, r -> {
			Thread thread = new Thread(r);
			thread.setDaemon(true);
			thread.setName("healthyCheckThread");
			return thread;
		});
	}
	
	public void start() {
		this.scheduledExecutorService.scheduleAtFixedRate(this::check, 5, this.checkInterval, TimeUnit.SECONDS);
	}
	
	public void check() {
		if (stop) {
			return;
		}
		try {
			Map<String, Object> healthDetails = new HashMap<>();
			healthIndicators.forEach((k, v) -> {
				final Health health = v.health();
				if (!health.getStatus().equals(Status.UP)) {
					healthDetails.put(k.replace("HealthIndicator", ""), health.getDetails());
				}
			});
			if (!healthDetails.isEmpty()) {
				Map<String, Object> params = new HashMap<>();
				params.put("appName", Apps.getAppName());
				params.put("env", Env.getEnv());
				params.put("host", IPUtil.getFirstNoLoopbackIPV4Address());
				params.put("healdthDetails", objectMapper.writeValueAsString(healthDetails));
				params.put("uptime", uptime);
				params.put("bootVersion", Versions.getVersion());
				params.put("bootCompileTime", Versions.getComplieTime());
				final StringBuilder builder = new StringBuilder(256);
				healthDetails.forEach((k, v) -> builder.append(k).append(" ---> ").append(v).append("\n"));
				logger.warn("应用健康状态异常: \n{}", builder.toString());
				if (notifyEnable) {
					HttpRequest request = HttpRequest.post(notifyAddress).connectTimeout(10000).readTimeout(10000)
						.form(params);
					if (request.code() != 200) {
						logger.error("发送健康状态失败: statusCode={}, message={}", request.code(), request.body());
					}
				}
			}
		} catch (Exception e) {
			logger.error("health check error", e);
		}
	}
	
	public String getNotifyAddress() {
		return notifyAddress;
	}
	
	public void setNotifyAddress(String notifyAddress) {
		this.notifyAddress = notifyAddress;
	}
	
	public boolean isNotifyEnable() {
		return notifyEnable;
	}
	
	public void setNotifyEnable(boolean notifyEnable) {
		this.notifyEnable = notifyEnable;
	}
	
	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		stop = true;
		if (this.scheduledExecutorService != null) {
			this.scheduledExecutorService.shutdownNow();
		}
	}
}
