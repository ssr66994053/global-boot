/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-28 10:01 创建
 *
 */
package com.global.boot.amqp;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yanglie@yiji.com
 */
@ConfigurationProperties(prefix = "yiji.rabbitmq.pool")
public class RabbitThreadPoolProperties {
	/**
	 * 可选：线程池核心线程数，默认为20
	 */
	private Integer corePoolSize = 20;
	/**
	 * 可选：线程池最大线程数，默认为100,支持hera动态修改
	 */
	private Integer maxPoolSize = 100;
	/**
	 * 可选：线程队列大小，默认为500
	 */
	private Integer queueCapacity = 500;
	
	public Integer getCorePoolSize() {
		return corePoolSize;
	}
	
	public void setCorePoolSize(Integer corePoolSize) {
		this.corePoolSize = corePoolSize;
	}
	
	public Integer getMaxPoolSize() {
		return maxPoolSize;
	}
	
	public void setMaxPoolSize(Integer maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}
	
	public Integer getQueueCapacity() {
		return queueCapacity;
	}
	
	public void setQueueCapacity(Integer queueCapacity) {
		this.queueCapacity = queueCapacity;
	}
}
