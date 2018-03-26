/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-27 16:04 创建
 *
 */
package com.global.boot.test.amqp.topic;

/**
 * @author yanglie@yiji.com
 */
public class MyMessage {
	private String id;
	private String message;
	private String routingKey;
	
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
	
	public String getRoutingKey() {
		return routingKey;
	}
	
	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}
}
