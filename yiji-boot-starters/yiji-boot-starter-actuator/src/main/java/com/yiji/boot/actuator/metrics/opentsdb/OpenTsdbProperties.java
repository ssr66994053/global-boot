/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-04-19 14:46 创建
 *
 */
package com.yiji.boot.actuator.metrics.opentsdb;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author daidai@yiji.com
 */
@ConfigurationProperties(prefix = OpenTsdbProperties.PREFIX)
public class OpenTsdbProperties {
	
	public static final String PREFIX = "yiji.metrics.opentsdb";
	
	public static final String EXCLUDED_METRICS = "mem," + "processors," + "instance.uptime," + "uptime,"
													+ "systemload.average," + "heap.committed," + "heap.init,"
													+ "heap," + "nonheap.committed," + "nonheap.init," + "nonheap,"
													+ "threads.peak," + "threads.daemon," + "threads.totalStarted,"
													+ "classes," + "classes.loaded," + "classes.unloaded,"
													+ "gc.ps_scavenge.count," + "gc.ps_scavenge.time,"
													+ "gc.ps_marksweep.count," + "gc.ps_marksweep.time,"
													+ "redis.session.MinIdle," + "redis.session.MaxIdle,"
													+ "redis.session.MaxTotal," + "redis.session.NumWaiters,"
													+ "redis.session.MeanBorrowWaitTimeMillis,"
													+ "redis.session.MaxBorrowWaitTimeMillis,"
													+ "redis.session.MaxWaitMillis," + "redis.cache.MinIdle,"
													+ "redis.cache.MaxIdle," + "redis.cache.MaxTotal,"
													+ "redis.cache.NumWaiters,"
													+ "redis.cache.MeanBorrowWaitTimeMillis,"
													+ "redis.cache.MaxBorrowWaitTimeMillis,"
													+ "redis.cache.MaxWaitMillis," + "fastdfs.tracker.MinIdlePerKey,"
													+ "fastdfs.tracker.MaxIdlePerKey," + "fastdfs.tracker.MaxTotal,"
													+ "fastdfs.tracker.MaxTotalPerKey," + "fastdfs.tracker.NumWaiters,"
													+ "fastdfs.tracker.MeanBorrowWaitTimeMillis,"
													+ "fastdfs.tracker.MaxBorrowWaitTimeMillis,"
													+ "fastdfs.tracker.MaxWaitMillis,"
													+ "fastdfs.storage.MinIdlePerKey,"
													+ "fastdfs.storage.MaxIdlePerKey," + "fastdfs.storage.MaxTotal,"
													+ "fastdfs.storage.MaxTotalPerKey," + "fastdfs.storage.NumWaiters,"
													+ "fastdfs.storage.MeanBorrowWaitTimeMillis,"
													+ "fastdfs.storage.MaxBorrowWaitTimeMillis,"
													+ "fastdfs.storage.MaxWaitMillis," + "tomcat.maxThreads,"
													+ "tomcat.maxConnections," + "tomcat.minSpareThreads,"
													+ "tomcat.connectionTimeout," + "tomcat.submittedTaskCount,"
													+ "tomcat.completedTaskCount," + "dubbo.Gauge.corePoolSize,";
	
	private static final Logger logger = LoggerFactory.getLogger(OpenTsdbProperties.class);
	
	/**
	 * OpenTsdb 地址url, 多个地址以","分隔,应用不用配置
	 */
	private String urls;
	
	public String getUrls() {
		return urls;
	}
	
	public void setUrls(String urls) {
		this.urls = urls;
	}
	
	public String getValidExcludes() {
		String excludes = EXCLUDED_METRICS;
		List<String> validExcludes = Lists.newArrayList();
		final List<String> customerMetrics = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(excludes);
		customerMetrics.forEach(m -> {
			if (isValidMetricName(m)) {
				validExcludes.add(m);
			} else {
				logger.error("配置({})不合法, 必须含有字母或数字!", m);
			}
		});
		return String.join(",", validExcludes);
	}
	
	private boolean isValidMetricName(String customerIncludes) {
		boolean hasNormalText = false;
		for (int i = 0; i < customerIncludes.length(); i++) {
			if (Character.isLetterOrDigit(customerIncludes.charAt(i))) {
				hasNormalText = true;
				break;
			}
		}
		return hasNormalText;
	}
	
	public String[] getValidateUrls() {
		final String urls = this.getUrls();
		String[] validateUrls = null;
		if (StringUtils.hasText(urls)) {
			List<String> urlList = Splitter.on(",").omitEmptyStrings().splitToList(urls);
			if (!urlList.isEmpty()) {
				validateUrls = urlList.toArray(new String[0]);
			}
		}
		return validateUrls;
	}
}
