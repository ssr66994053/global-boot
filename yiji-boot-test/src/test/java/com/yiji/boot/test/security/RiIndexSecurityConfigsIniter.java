/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * LiXiang 2016年4月25日 下午6:45:52 创建
 */
package com.yiji.boot.test.security;

import com.yiji.boot.securityframework.IndexProperties;
import com.yiji.common.security.index.mode.def.DefaultIndexBuilderFactory;
import com.yiji.common.security.referenceimplements.SecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * 用于RI索引环境初始化安全配置的初始化器。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
@DependsOn(IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "SecurityConfigs")
//@Component
public class RiIndexSecurityConfigsIniter {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired(required = false)
	private Map<String, SecurityConfig> scs;
	
	@PostConstruct
	public void init() throws Exception {
		this.logger.info("初始化RI索引环境初始化安全配置。");
		SecurityConfig securityConfig = new SecurityConfig();
		securityConfig.setSecurityUser("default");
		securityConfig.setIndexCharset(Charset.forName("UTF-8"));
		securityConfig.setIndexMode(DefaultIndexBuilderFactory.MODE);
		scs.put("default", securityConfig);
	}
	
}
