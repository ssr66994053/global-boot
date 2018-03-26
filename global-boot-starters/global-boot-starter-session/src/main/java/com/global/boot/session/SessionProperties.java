/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-21 17:35 创建
 *
 */

package com.global.boot.session;

import com.global.boot.core.Apps;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yanglie@yiji.com
 */
@ConfigurationProperties(prefix = "yiji.session")
public class SessionProperties {
	private boolean enable = true;
	/**
	 * 必填：访问地址
	 */
	private String host = "localhost";
	/**
	 * 必填：访问端口
	 */
	private int port = 6379;
	/**
	 * 应用缓存空间名
	 */
	private final String namespace = "session_" + Apps.getAppName();
	
	/**
	 * sesssion过期时间,单位秒
	 */
	private int expiredTimeOut = 1800;
	/**
	 * 可选：创建socket连接的超时时间
	 */
	private int connectTimeOut = 5000;
	
	/**
	 * 可选: 支持从url中获取session
	 */
	private boolean enableUrlBasedSession = false;
	/**
	 * 可选：连接池配置
	 */
	private Pool pool = new Pool();
	
	public static class Pool {
		/**
		 * 最大连接数
		 */
		private int maxTotal = 500;
		/**
		 * 最大空闲连接
		 */
		private int maxIdle = 8;
		/**
		 * 最大等待连接时间
		 */
		private int maxWait = 2000;
		
		public int getMaxIdle() {
			return maxIdle;
		}
		
		public void setMaxIdle(int maxIdle) {
			this.maxIdle = maxIdle;
		}
		
		public int getMaxTotal() {
			return maxTotal;
		}
		
		public void setMaxTotal(int maxTotal) {
			this.maxTotal = maxTotal;
		}
		
		public int getMaxWait() {
			return maxWait;
		}
		
		public void setMaxWait(int maxWait) {
			this.maxWait = maxWait;
		}
	}
	
	public boolean isEnable() {
		return enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public Pool getPool() {
		return pool;
	}
	
	public void setPool(Pool pool) {
		this.pool = pool;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public int getConnectTimeOut() {
		return connectTimeOut;
	}
	
	public void setConnectTimeOut(int connectTimeOut) {
		this.connectTimeOut = connectTimeOut;
	}
	
	public int getExpiredTimeOut() {
		return expiredTimeOut;
	}
	
	public void setExpiredTimeOut(int expiredTimeOut) {
		this.expiredTimeOut = expiredTimeOut;
	}
	
	public boolean isEnableUrlBasedSession() {
		return enableUrlBasedSession;
	}
	
	public void setEnableUrlBasedSession(boolean enableUrlBasedSession) {
		this.enableUrlBasedSession = enableUrlBasedSession;
	}
}
