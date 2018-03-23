package com.global.boot.amqp;/*
* www.yiji.com Inc.
* Copyright (c) 2014 All Rights Reserved
*/

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-27 13:51 创建
 *
 */

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yanglie@yiji.com
 */

@ConfigurationProperties(prefix = "yiji.rabbitmq")
public class RabbitExtensionProperties {
	private boolean enable = true;
	/**
	 * 可选：建连接超时时间，单位微秒，10s
	 */
	private Integer connectionTimeout = 10000;
	/**
	 * 可选：rabbit connection factory缓存channel数量，默认为5
	 */
	private Integer cacheChannelSize = 5;
	
	/**
	 * 可选：listener并发数
	 */
	private Integer listenerConcurrency = 5;
	
	public Integer getCacheChannelSize() {
		return cacheChannelSize;
	}
	
	public void setCacheChannelSize(Integer cacheChannelSize) {
		this.cacheChannelSize = cacheChannelSize;
	}
	
	public boolean isEnable() {
		return enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	public Integer getConnectionTimeout() {
		return connectionTimeout;
	}
	
	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	
	public Integer getListenerConcurrency() {
		return listenerConcurrency;
	}
	
	public void setListenerConcurrency(Integer listenerConcurrency) {
		this.listenerConcurrency = listenerConcurrency;
	}
}
