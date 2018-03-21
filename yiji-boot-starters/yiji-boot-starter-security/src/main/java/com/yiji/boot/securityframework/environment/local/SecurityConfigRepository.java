/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * Agrael 2015年9月8日 下午3:01:01 创建
 */
package com.yiji.boot.securityframework.environment.local;

import com.yiji.common.security.SecurityManager;
import com.yiji.common.security.referenceimplements.SecurityConfig;

/**
 * <code>local</code>环境创建的 {@link SecurityConfig} 的仓库，当为本地提供 {@link SecurityManager} 时，需要实现该仓库。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
public interface SecurityConfigRepository {
	
	/**
	 * 通过安全用户得到对应的安全配置信息。
	 * @param securityUser 安全用户。
	 * @return 安全用户对应的安全配置信息。
	 */
	SecurityConfig getSecurityConfig(String securityUser);
	
}
