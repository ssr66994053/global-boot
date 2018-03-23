/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */
package com.global.boot.core;

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-04 18:39 创建
 *
 */

/**
 * @author qiubo@yiji.com
 */
public class AppConfigException extends RuntimeException {
	public AppConfigException(Throwable cause) {
		super(cause);
	}
	
	public AppConfigException(String message) {
		super(message);
	}
	
	public AppConfigException(String message, Throwable cause) {
		super(message, cause);
	}
	
	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}
