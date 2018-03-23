/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-21 11:16 创建
 *
 */
package com.global.boot.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 通用配置
 * @author qiubo@yiji.com
 */
@ConfigurationProperties(CommonProperties.PREFIX)
public class CommonProperties {
	public static final String PREFIX = "yiji.common";
	
	/**
	 * zk配置信息
	 */
	private String zkUrl;
	
	public String getZkUrl() {
		return zkUrl;
	}
	
	public void setZkUrl(String zkUrl) {
		this.zkUrl = zkUrl;
	}
}
