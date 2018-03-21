/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-09-15 15:08 创建
 *
 */
package com.yiji.boot.fastdfs;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yanglie@yiji.com
 */
@ConfigurationProperties(FastdfsProperties.PREFIX)
public class FastdfsProperties {
	public static final String PREFIX = "yiji.fastdfs";
	
	/**
	 * 是否启用此组件
	 */
	private boolean enable = true;
	
	/**
	 * 必填：服务器地址和端口, 如: 127.0.0.1:22122
	 */
	private String trackerAddress = "localhost:22122";
	/**
	 * 必填: 文件资源下载域名，如fastdfs.yijifu.net
	 */
	private String downloadHost = "127.0.0.1:80";
	
	/**
	 * 可选: 加密文件资源下载域名，如ppm.yijifu.net
	 */
	private String encryptedDownloadHost = "127.0.0.1:80";
	/**
	 * 可选: 所以对文件进行加密的组，多个组以逗号分隔
	 */
	private String encryptedGroups = null;
	/**
	 * 可选: 创建连接超时时间，单位second, 默认5s
	 */
	private Integer connectTimeout = 5;
	/**
	 * 可选: 数据连接超时时间，单位second，默认30s
	 */
	private Integer networkTimeOut = 30;
	
	/**
	 * 可选: 设置了healthCheckGroup的话，组件健康检查时，只检查指定group存储服务
	 */
	private String healthCheckGroup = null;
	
	/**
	 * 连接池配置
	 */
	private Pool pool = new Pool();
	
	public String getTrackerAddress() {
		return trackerAddress;
	}
	
	public void setTrackerAddress(String trackerAddress) {
		this.trackerAddress = trackerAddress;
	}
	
	public Integer getConnectTimeout() {
		return connectTimeout;
	}
	
	public void setConnectTimeout(Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	
	public Integer getNetworkTimeOut() {
		return networkTimeOut;
	}
	
	public void setNetworkTimeOut(Integer networkTimeOut) {
		this.networkTimeOut = networkTimeOut;
	}
	
	public Pool getPool() {
		return pool;
	}
	
	public void setPool(Pool pool) {
		this.pool = pool;
	}
	
	public String getDownloadHost() {
		return downloadHost;
	}
	
	public void setDownloadHost(String downloadHost) {
		this.downloadHost = downloadHost;
	}
	
	public boolean isEnable() {
		return enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	public String getHealthCheckGroup() {
		return healthCheckGroup;
	}
	
	public void setHealthCheckGroup(String healthCheckGroup) {
		this.healthCheckGroup = healthCheckGroup;
	}
	
	public String getEncryptedDownloadHost() {
		return encryptedDownloadHost;
	}
	
	public void setEncryptedDownloadHost(String encryptedDownloadHost) {
		this.encryptedDownloadHost = encryptedDownloadHost;
	}
	
	public String getEncryptedGroups() {
		return encryptedGroups;
	}
	
	public void setEncryptedGroups(String encryptedGroups) {
		this.encryptedGroups = encryptedGroups;
	}
	
	public static class Pool {
		/**
		 * 可选: 最大连接数, 默认为200
		 */
		private Integer maxTotal = 200;
		/**
		 * 可选：单个服务结点最大连接数，默认为3
		 */
		private Integer maxTotalPerNode = 3;
		/**
		 * 可选：单个服务结点最大空闲连接数，默认为1
		 */
		private Integer maxIdlePerNode = 1;
		
		public Integer getMaxTotal() {
			return maxTotal;
		}
		
		public void setMaxTotal(Integer maxTotal) {
			this.maxTotal = maxTotal;
		}
		
		public Integer getMaxTotalPerNode() {
			return maxTotalPerNode;
		}
		
		public void setMaxTotalPerNode(Integer maxTotalPerNode) {
			this.maxTotalPerNode = maxTotalPerNode;
		}
		
		public Integer getMaxIdlePerNode() {
			return maxIdlePerNode;
		}
		
		public void setMaxIdlePerNode(Integer maxIdlePerNode) {
			this.maxIdlePerNode = maxIdlePerNode;
		}
	}
}
