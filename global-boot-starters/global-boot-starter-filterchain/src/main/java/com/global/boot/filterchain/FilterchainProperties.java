/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 *
 * 修订记录:
 * zhouxi@yiji.com 2015-09-15 15:41 创建
 *
 */
package com.global.boot.filterchain;

import com.google.common.base.Strings;
import com.global.boot.core.Apps;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qiubo@yiji.com
 */
@ConfigurationProperties("yiji.filterchain")
public class FilterchainProperties {
	
	private static final String DEFAULT_SCAN_PACKAGE = Apps.getBasePackage() + "," + "com.global.boot";
	/**
	 * 是否启用组件
	 */
	private boolean enable = true;
	
	/**
	 * filter扫描包,多个包之间用逗号隔开,默认使用应用base package
	 */
	private String scanPackage;
	
	public boolean isEnable() {
		return enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	public String getScanPackage() {
		if (Strings.isNullOrEmpty(scanPackage)) {
			return DEFAULT_SCAN_PACKAGE;
		} else {
			return DEFAULT_SCAN_PACKAGE + "," + scanPackage.trim();
		}
	}
	
	public void setScanPackage(String scanPackage) {
		this.scanPackage = scanPackage;
	}
}
