/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * LiXiang 2015年8月17日 下午3:58:29 创建
 */
package com.yiji.boot.securityframework.environment.local;

import com.yiji.common.security.referenceimplements.RIMessageDigestSecurityModeFactory;
import com.yiji.common.security.referenceimplements.SecurityConfig;

/**
 * <code>local</code>环境创建的单独消息摘要模式工厂的实现。
 * 
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 * 
 */
public class LocalMessageDigestSecurityModeFactory extends RIMessageDigestSecurityModeFactory {
	
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
