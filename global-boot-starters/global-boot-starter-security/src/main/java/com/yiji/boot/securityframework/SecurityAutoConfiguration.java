/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * Agrael 2015年9月8日 上午11:37:46 创建
 */
package com.yiji.boot.securityframework;

import com.yiji.boot.core.AutoConfigurationProperties;
import com.yiji.boot.securityframework.SecurityProperties.SyphonProperties;
import com.yiji.boot.securityframework.environment.local.LocalSecurityManagerFactory;
import com.yiji.boot.securityframework.environment.local.SecurityConfigRepository;
import com.yiji.boot.securityframework.environment.ri.RiSecurityManagerFactory;
import com.yiji.common.security.ModeDistinguishSecurityManager;
import com.yiji.common.security.SecurityConfigurationException;
import com.yiji.common.security.SecurityManager;
import com.yiji.common.security.bean.JavaBeanSyphon;
import com.yiji.common.security.bean.Syphon;
import com.yiji.common.security.referenceimplements.SecurityConfig;
import com.yjf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 安全框架核心的功能自动配置。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
@Configuration
@EnableConfigurationProperties({ SecurityProperties.class })
@ConditionalOnProperty(value = SecurityProperties.PATH + ".enable", matchIfMissing = true)
public class SecurityAutoConfiguration implements ApplicationContextAware {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityAutoConfiguration.class);
	
	private ApplicationContext applicationContext;
	
	@Autowired
	private SecurityProperties securityProperties;
	
	private Map<String, SecurityConfig> securityConfigs;
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	/**
	 * 在<code>ri</code>环境下创建一个名为<code>yijiBoot_SF_SecurityConfigs</code>的 安全配置印射 到容器。
	 * @return 创建的实例。
	 */
	@Bean(name = SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "SecurityConfigs")
	@ConditionalOnProperty(name = SecurityProperties.PATH + ".securityManagerEnv", havingValue = "ri",
			matchIfMissing = true)
	@ConditionalOnMissingBean(name = SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "SecurityConfigs")
	public Map<String, SecurityConfig> createRiSecurityConfigs() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("[ri]环境，系统不存在名为[{}]的Bean，创建类型为'{}'的Bean到容器。", SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX
																		+ "SecurityConfigs",
				ConcurrentHashMap.class.getName());
		}
		this.securityConfigs = new ConcurrentHashMap<String, SecurityConfig>();
		return this.securityConfigs;
	}
	
	/**
	 * 在<code>ri</code>环境下创建一个名为<code>yijiBoot_SF_SecurityManager</code>的 {@link SecurityManager} 到容器。
	 * @return 创建的实例。
	 */
	@Bean(name = SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "SecurityManager")
	@DependsOn(SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "SecurityConfigs")
	@ConditionalOnProperty(name = SecurityProperties.PATH + ".securityManagerEnv", havingValue = "ri",
			matchIfMissing = true)
	@ConditionalOnMissingBean(name = SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "SecurityManager")
	public SecurityManager createRiSecurityManager() {
		ModeDistinguishSecurityManager securityManager = new RiSecurityManagerFactory()
			.createSecurityManager(this.securityConfigs);
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("[ri]环境，系统不存在名为[{}]的Bean，创建类型为'{}'的Bean到容器。", SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX
																		+ "SecurityManager", securityManager.getClass()
				.getName());
		}
		return securityManager;
	}
	
	/**
	 * 在<code>local</code>环境下创建一个名为<code>yijiBoot_SF_SecurityManager</code>的 {@link SecurityManager} 到容器。
	 * @return 创建的实例。
	 */
	@Bean(name = SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "SecurityManager")
	@ConditionalOnProperty(name = SecurityProperties.PATH + ".securityManagerEnv", havingValue = "local")
	@ConditionalOnMissingBean(name = SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "securityManager")
	@ConditionalOnBean(SecurityConfigRepository.class)
	public SecurityManager createLocalSecurityManager() {
		SecurityConfigRepository securityConfigRepository;
		try {
			securityConfigRepository = this.applicationContext.getBean(SecurityConfigRepository.class);
		} catch (NoUniqueBeanDefinitionException e) {
			throw new SecurityConfigurationException("[local]环境的[SecurityManager]创建时依赖发生错误：["
														+ SecurityConfigRepository.class.getName() + "]类型的Bean存在多个匹配项。");
		}
		ModeDistinguishSecurityManager securityManager = new LocalSecurityManagerFactory()
			.createSecurityManager(securityConfigRepository);
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("[local]环境，系统不存在名为[{}]的Bean，创建类型为'{}'的Bean到容器。",
				SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "SecurityManager", securityManager.getClass()
					.getName());
		}
		return securityManager;
	}
	
	/**
	 * 创建一个名为<code>yijiBoot_SF_Syphon</code>的 {@link Syphon} 到容器。
	 * @return 创建到容器的 {@link Syphon} 的实例。
	 */
	@Bean(name = SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "Syphon")
	@ConditionalOnProperty(value = SyphonProperties.PATH + ".enable", matchIfMissing = true)
	@ConditionalOnMissingBean(name = SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "Syphon")
	@ConditionalOnBean(SecurityManager.class)
	public Syphon createSyphon() {
		boolean supportProtected = this.securityProperties.getSyphonProperties().isSupportProtected();
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(
				"系统不存在[{}]的Bean，创建名称为'{}'类型为'{}'的Bean到容器，["
						+ AutoConfigurationProperties.mergePropertyPath(SyphonProperties.PATH, "supportProtected")
						+ "]为'{}'。", Syphon.class.getName(),
				SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "Syphon", JavaBeanSyphon.class.getName(),
				supportProtected);
		}
		JavaBeanSyphon javaBeanSyphon = new JavaBeanSyphon(supportProtected);
		if (StringUtils.isBlank(this.securityProperties.getSecurityManagerRefName())) {
			javaBeanSyphon.setSecurityManager(BeanUtils.getBean(this.applicationContext, SecurityManager.class,
				this.securityProperties, "securityManagerRefName"));
		} else {
			SecurityManager securityManager = BeanUtils.getBean(this.applicationContext,
				this.securityProperties.getSecurityManagerRefName(), SecurityManager.class, this.securityProperties,
				"securityManagerRefName");
			javaBeanSyphon.setSecurityManager(securityManager);
		}
		return javaBeanSyphon;
	}
	
}
