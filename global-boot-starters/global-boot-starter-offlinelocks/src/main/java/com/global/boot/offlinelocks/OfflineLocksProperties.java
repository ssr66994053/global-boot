/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * LiXiang 2016年2月18日 下午2:48:16 创建
 */
package com.yiji.boot.offlinelocks;

import com.yiji.boot.core.Apps;
import com.yiji.boot.core.AutoConfigurationProperties;
import com.yiji.concurrent.offlinelocks.LockManager;
import com.yiji.concurrent.offlinelocks.distributed.zookeeperclient.ZooKeeperLockManager;
import com.yiji.concurrent.offlinelocks.vm.SimpleObjectVMLockManager;
import com.yjf.common.lang.ip.IPUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * 离线锁插件的配置属性信息。 {@link LockManager} 的来源环境默认支持如下环境：
 * <ul>
 * <li>vm：使用 {@link SimpleObjectVMLockManager} 作为锁管理器。yiji.offlinelocks.fairLock属性可以设置是否使用公平锁。</li>
 * <li>zookeeper：使用 {@link ZooKeeperLockManager} 作为锁管理器<strong>这也是默认的环境。</strong>。需要<tt>Zookeeper</tt> 支持，并且配置如下属性：
 * <ul>
 * <li>yiji.common.zkUrl：<tt>Zookeeper</tt>的连接属性。</li>
 * <li>yiji.offlinelocks.serverTimeout：服务器超时时间（单位为毫秒），如果小于等于0则表示使用默认超时时间。（可选）</li>
 * <li>yiji.offlinelocks.lockTimeout：锁超时时间（单位为毫秒），如果小于等于0则表示锁不超时。（可选）</li>
 * </ul>
 * </li>
 * </ul>
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
@ConfigurationProperties(OfflineLocksProperties.PATH)
public class OfflineLocksProperties implements AutoConfigurationProperties, Serializable {
	
	private static final long serialVersionUID = -9216616214413641303L;
	
	/** boot环境中创建带name的bean的name前缀 */
	public static final String BOOT_CREATE_BEAN_NAME_PREFIX = "yijiBoot_OL_";
	
	/** 表达式取得该类实例的路径 */
	public static final String PATH = "yiji.offlinelocks";
	
	/** 是否启用离线锁的功能，true为启用 */
	private boolean enable = true;
	
	/** 表达式取得该对象的路径 */
	private final String path = PATH;
	
	/** LockManager的实现方式,默认为基于zk的实现,可选参数vm用于测试 */
	private String lockManagerEnv = "zk";
	
	/** 锁管理器的根目录（如果需要） */
	private String rootDir = "/offlinelocks";
	
	/** 锁管理器的组 */
	private String group = Apps.getAppName();
	
	/** 锁管理器的id */
	private String id = IPUtil.getFirstNoLoopbackIPV4Address();
	
	/** 使用的编码 */
	private String encodeCharset = "UTF-8";
	
	/** 服务器超时时间（单位为毫秒），如果小于等于0则表示使用默认超时时间 */
	private long serverTimeout = 30000l;
	
	/** 锁超时时间（单位为毫秒），如果小于等于0则表示锁不超时 */
	private long lockTimeout = -1;
	
	/** 是否使用公平锁，为true表示使用公平锁 */
	private boolean fairLock;
	
	public String getPath() {
		return this.path;
	}
	
	/**
	 * 判定是否启用离线锁的功能。
	 * @return true为启用。
	 */
	public boolean isEnable() {
		return this.enable;
	}
	
	/**
	 * 设置是否启用离线锁的功能。
	 * @param enable true为启用。
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	/**
	 * 得到{@link com.yiji.concurrent.offlinelocks.LockManager} 的来源环境。
	 * @return {@link com.yiji.concurrent.offlinelocks.LockManager} 的来源环境。
	 */
	public String getLockManagerEnv() {
		return this.lockManagerEnv;
	}
	
	/**
	 * 设置{@link com.yiji.concurrent.offlinelocks.LockManager} 的来源环境。
	 * @param lockManagerEnv {@link com.yiji.concurrent.offlinelocks.LockManager} 的来源环境
	 */
	public void setLockManagerEnv(String lockManagerEnv) {
		this.lockManagerEnv = lockManagerEnv;
	}
	
	/**
	 * 得到锁管理器的根目录（如果需要）。
	 * @return 锁管理器的根目录。
	 */
	public String getRootDir() {
		return this.rootDir;
	}
	
	/**
	 * 得到锁管理器的组。
	 * @return 锁管理器的组。
	 */
	public String getGroup() {
		return this.group;
	}
	
	/**
	 * 得到锁管理器的id。
	 * @return 锁管理器的id。
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * 得到使用的编码。
	 * @return 使用的编码。
	 */
	public String getEncodeCharset() {
		return this.encodeCharset;
	}
	
	/**
	 * 得到服务器超时时间（单位为毫秒），如果小于等于0则表示使用默认超时时间。
	 * @return 服务器超时时间。
	 */
	public long getServerTimeout() {
		return this.serverTimeout;
	}
	
	/**
	 * 设置服务器超时时间（单位为毫秒），如果小于等于0则表示使用默认超时时间。
	 * @param serverTimeout 服务器超时时间。
	 */
	public void setServerTimeout(long serverTimeout) {
		this.serverTimeout = serverTimeout;
	}
	
	/**
	 * 得到锁超时时间（单位为毫秒），如果小于等于0则表示锁不超时 。
	 * @return 锁超时时间。
	 */
	public long getLockTimeout() {
		return this.lockTimeout;
	}
	
	/**
	 * 设置锁超时时间（单位为毫秒），如果小于等于0则表示锁不超时 。
	 * @param lockTimeout 锁超时时间 。
	 */
	public void setLockTimeout(long lockTimeout) {
		this.lockTimeout = lockTimeout;
	}
	
	/**
	 * 判断是否使用公平锁 。
	 * @return 为true表示使用公平锁
	 */
	public boolean isFairLock() {
		return this.fairLock;
	}
	
	/**
	 * 设置是否使用公平锁 。
	 * @param fairLock 为true表示使用公平锁
	 */
	public void setFairLock(boolean fairLock) {
		this.fairLock = fairLock;
	}
	
	@Override
	public String toString() {
		return "OfflineLocksProperties{" + "lockManagerEnv='" + lockManagerEnv + '\'' + ", rootDir='" + rootDir + '\''
				+ ", group='" + group + '\'' + ", id='" + id + '\'' + ", encodeCharset='" + encodeCharset + '\''
				+ ", serverTimeout=" + serverTimeout + ", lockTimeout=" + lockTimeout + ", fairLock=" + fairLock + '}';
	}
}
