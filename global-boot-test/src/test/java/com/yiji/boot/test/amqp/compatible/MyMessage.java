/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-09-15 13:02 创建
 *
 */
package com.yiji.boot.test.amqp.compatible;

/**
 * @author yanglie@yiji.com
 */
public class MyMessage {
	private String id;
	private String message;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
