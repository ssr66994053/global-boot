/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved.
 */

/*
 * 修订记录：
 * LiXiang 2016年2月18日 下午11:56:22 创建
 */
package com.yiji.boot.offlinelocks;

import com.yjf.common.lang.ConfigurationException;

/**
 * 指示一个严重的配置错误。
 * 
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 * 
 */
public class LockConfigurationException extends ConfigurationException {
	
	private static final long serialVersionUID = -4027183430884233742L;
	
	/**
	 * 构造一个LockConfigurationException。
	 */
	public LockConfigurationException() {
		super();
	}
	
	/**
	 * 构造一个LockConfigurationException。
	 * @param msg 详细消息。
	 */
	public LockConfigurationException(String msg) {
		super(msg);
	}

	public LockConfigurationException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
