/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2015-09-22 14:51 创建
 *
 */
package com.global.boot.postman;

import com.global.boot.core.Apps;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * postman 组件属性。
 * @see <a href=http://192.168.45.16/confluence/pages/viewpage.action?pageId=10584156 >Postman接入指南</a>
 * @author daidai@yiji.com
 */
@ConfigurationProperties(PostmanProperties.PREFIX)
public class PostmanProperties {
	public static final String PREFIX = "yiji.postman";
	
	/**
	 * 是否启用postman
	 */
	private boolean enable = true;
	/**
	 * 必填：信息中心分配的appkey，默认为应用名称
	 */
	private String appkey = Apps.getAppName();
	
	/**
	 * 必填：信息中心分配的应用appsecret
	 */
	private String appsecret;
	
	/**
	 * 可选：post man的服务地址，默认为: https://postman.yiji.com/services
	 */
	private String serviceUrl = "https://postman.yiji.com/services";
	
	private int maxPoolSize = 30;
	
	public boolean isEnable() {
		return enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	public String getAppkey() {
		return appkey;
	}
	
	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}
	
	public String getAppsecret() {
		return appsecret;
	}
	
	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}
	
	public String getServiceUrl() {
		return serviceUrl;
	}
	
	public String getHealthCheckUrl() {
		// health check url   https://postman.yiji.com/health
		return getServiceUrl().replace("/services", "/health");
	}
	
	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
	
	public int getMaxPoolSize() {
		return maxPoolSize;
	}
	
	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}
}
