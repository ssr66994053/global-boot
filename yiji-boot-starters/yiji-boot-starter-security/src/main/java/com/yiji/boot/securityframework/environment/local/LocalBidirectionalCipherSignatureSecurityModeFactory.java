/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * Agrael 2015年9月8日 下午4:40:46 创建
 */
package com.yiji.boot.securityframework.environment.local;

import com.yiji.common.security.referenceimplements.RIBidirectionalCipherSignatureSecurityModeFactory;
import com.yiji.common.security.referenceimplements.SecurityConfig;

/**
 * <code>local</code>环境创建的双向（加密、解密）的“双向加密解密/双向签名/密文签名/直接对比签名验证”安全模式工厂实现。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
public class LocalBidirectionalCipherSignatureSecurityModeFactory extends
																	RIBidirectionalCipherSignatureSecurityModeFactory {
	
	/** SecurityConfig 的仓库 */
	private SecurityConfigRepository securityConfigRepository;
	
	/**
	 * 设置SecurityConfig 的仓库。
	 * @param securityConfigRepository SecurityConfig 的仓库
	 */
	public void setSecurityConfigRepository(SecurityConfigRepository securityConfigRepository) {
		this.securityConfigRepository = securityConfigRepository;
	}
	
	@Override
	protected SecurityConfig getSecurityConfig(String securityUser) {
		if (securityUser == null) {
			return null;
		}
		return this.securityConfigRepository.getSecurityConfig(securityUser);
	}
	
}
