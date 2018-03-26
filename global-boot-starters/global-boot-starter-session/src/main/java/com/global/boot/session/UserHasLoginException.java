/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-05-03 17:08 创建
 */
package com.global.boot.session;

/**
 * @author qiubo@yiji.com
 */
public class UserHasLoginException extends Exception {
	private String username;
	private long loginTime;
	private String ip;
	
	public UserHasLoginException(String username, long loginTime, String ip) {
		this.loginTime = loginTime;
		this.username = username;
		this.ip = ip;
	}
	
	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public long getLoginTime() {
		return loginTime;
	}
	
	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("UserHasLoginException{");
		sb.append("ip='").append(ip).append('\'');
		sb.append(", username='").append(username).append('\'');
		sb.append(", loginTime='").append(loginTime).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
