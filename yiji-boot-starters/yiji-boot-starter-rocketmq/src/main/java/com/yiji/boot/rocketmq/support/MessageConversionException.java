/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-02-01 15:20 创建
 *
 */
package com.yiji.boot.rocketmq.support;

/**
 * @author yanglie@yiji.com
 */
public class MessageConversionException extends RuntimeException {
	public MessageConversionException(String message) {
		super(message);
	}
	
	public MessageConversionException(Throwable cause) {
		super(cause);
	}
	
	public MessageConversionException(String message, Throwable cause) {
		super(message, cause);
	}
}
