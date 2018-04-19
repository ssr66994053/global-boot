/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-02-23 10:23 创建
 */
package com.global.boot.actuator.metrics.file;

import com.alibaba.fastjson.JSON;
import com.global.common.log.BusinessLogger;
import com.global.common.log.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author qiubo@yiji.com
 */
public class MetricsFileWriter implements InitializingBean, DisposableBean {
	@Autowired
	private MetricsEndpoint metricsEndpoint;
	public static final String METRICS_LOG = "METRICS-LOG";
	private static final Logger logger = LoggerFactory.getLogger(METRICS_LOG);
	private ScheduledExecutorService service;
	
	@Override
	public void destroy() throws Exception {
		if (service != null) {
			service.shutdownNow();
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		service = Executors.newScheduledThreadPool(1, new CustomizableThreadFactory("MetricsFileWriter"));
		service.scheduleAtFixedRate(() -> {
			Map<String, Object> result = metricsEndpoint.invoke();
			BusinessLogger.BusinessDO businessDO = new BusinessLogger.BusinessDO();
			businessDO.setLogType("public-metric");
			result.forEach(businessDO::addContent);
			String json = JSON.toJSONString(businessDO);
			logger.info("{}", json);
		}, 0, 15, TimeUnit.SECONDS);
	}
}
