/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-25 11:25 创建
 *
 */
package com.global.boot.csrf;

/**
 * @author qiubo@yiji.com
 */
public class AccessDeniedException extends RuntimeException {
	public AccessDeniedException(String msg) {
		super(msg);
	}
}
