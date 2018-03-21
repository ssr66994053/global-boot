/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * Agrael 2015年9月8日 下午4:45:22 创建
 */
package com.yiji.boot.securityframework.environment.local;

import com.yiji.common.security.SecurityManager;
import com.yiji.common.security.SecurityModeFactory;
import com.yiji.common.security.SecurityProcessException;
import com.yiji.common.security.StandardModeDistinguishSecurityManager;
import com.yiji.common.security.referenceimplements.SecurityConfig;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <code>local</code>环境创建的 {@link SecurityManager} 实现。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
public class LocalSecurityManagerImpl extends StandardModeDistinguishSecurityManager {
	
	private SecurityConfigRepository securityConfigRepository;
	
	private ConcurrentMap<String, SecurityModeFactory> securityModeFactorys = new ConcurrentHashMap<String, SecurityModeFactory>();
	
	/**
	 * 设置SecurityConfig 的仓库。
	 * @param securityConfigRepository SecurityConfig 的仓库
	 */
	public void setSecurityConfigRepository(SecurityConfigRepository securityConfigRepository) {
		this.securityConfigRepository = securityConfigRepository;
	}
	
	/**
	 * 设置支持的安全模式的工厂列表。
	 * @param securityModeFactorys 支持安全模式工厂列表。
	 */
	public void setSecurityModeFactorys(ConcurrentMap<String, SecurityModeFactory> securityModeFactorys) {
		this.securityModeFactorys = securityModeFactorys;
	}
	
	public SecurityModeFactory getSecurityModeFactory(String securityUser) {
		SecurityConfig securityConfig = getSecurityConfig(securityUser);
		if (securityConfig == null) {
			throw new SecurityProcessException("没有安全用户'" + securityUser + "'。");
		}
		SecurityModeFactory securityModeFactory = this.securityModeFactorys.get(securityConfig.getMode());
		if (securityModeFactory == null) {
			throw new SecurityProcessException("没有安全模式'" + securityConfig.getMode() + "'。");
		}
		return securityModeFactory;
	}
	
	/**
	 * 通过安全用户得到对应的安全配置信息。
	 * @param securityUser 安全用户。
	 * @return 安全用户对应的安全配置信息。
	 */
	protected SecurityConfig getSecurityConfig(String securityUser) {
		if (securityUser == null) {
			return null;
		}
		return this.securityConfigRepository.getSecurityConfig(securityUser);
	}
	
}
