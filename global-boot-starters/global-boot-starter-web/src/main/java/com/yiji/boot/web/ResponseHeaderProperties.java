/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-10-15 10:08 创建
 *
 */
package com.yiji.boot.web;

import com.google.common.collect.Maps;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 定义一些通用的响应header
 * @author qiubo@yiji.com
 */
@ConfigurationProperties("yiji.resp")
public class ResponseHeaderProperties {
	
	private Map<String, String> headers = Maps.newHashMapWithExpectedSize(2);
	
	public ResponseHeaderProperties() {
		//		headers.put("x-frame-options", "SAMEORIGIN");
		headers.put("x-xss-protection", "1; mode=block");
		headers.put("Cache-Control","no-cache");
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
}
