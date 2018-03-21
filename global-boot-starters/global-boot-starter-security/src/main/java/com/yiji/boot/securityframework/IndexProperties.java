/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved.
 */

/*
 * 修订记录：
 * Agrael 2016年4月25日 下午6:27:18 创建
 */
package com.yiji.boot.securityframework;

import com.yiji.boot.core.Apps;
import com.yiji.boot.core.AutoConfigurationProperties;
import com.yiji.boot.securityframework.annotation.Sensitive;
import com.yiji.boot.securityframework.index.IndexAnnotationAdvice;
import com.yiji.common.security.annotation.Index;
import com.yiji.common.security.annotation.NeedIndex;
import com.yiji.common.security.annotation.ReverseIndex;
import com.yiji.framework.hera.client.support.annotation.AutoConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * 安全框架索引相关的功能的配置属性信息。 {@link com.yiji.common.security.index.IndexManager} 的来源环境默认支持如下环境：
 * <ul>
 * <li>ri：使用参考实现来提供 {@link com.yiji.common.security.index.IndexManager} （beanName为
 * <code>yijiBoot_SF_I_IndexManager</code>），通过使用 <tt>beanName</tt>： <code>yijiBoot_SF_I_SecurityConfigs</code>从容器中获得对应的
 * <code>java.util.Map<java.lang.String, com.yiji.common.security.referenceimplements.SecurityConfig></code>
 * 配置相关的安全用户信息。</li>
 * <li>ref：直接引用环境中已经存在或者已经配置的 {@link com.yiji.common.security.index.IndexManager} 实例。如果为该环境，则需要指定
 * {@link #setIndexManagerRefName(String)} 。</li>
 * <li>domain：使用安全域策略。这也是默认的配置。</li>
 * </ul>
 * 如果启用了{@link Index}、 {@link NeedIndex} 与 {@link ReverseIndex}的支持，则会创建一个名为
 * <code>yijiBoot_SF_I_IndexAnnotationAdvice</code> 的 {@link IndexAnnotationAdvice} 实例放入容器中。可以对其自行引用用来完成索引。如果开启索引所有的对象（
 * {@link #isIndexAllSensitiveMethod()} 为<code>true</code>），则 <code>yijiBoot_SF_I_IndexAnnotationAdvice</code>会默认用来索引被
 * {@link NeedIndex} 与 {@link ReverseIndex}注解了的对象。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
@ConfigurationProperties(IndexProperties.PATH)
public class IndexProperties implements AutoConfigurationProperties, Serializable {
	
	private static final long serialVersionUID = -9148080789781041329L;
	
	/** 表达式取得该类实例的路径 */
	public static final String PATH = "yiji.security.index";
	
	/** boot环境中创建带name的bean的name前缀 */
	public static final String BOOT_CREATE_BEAN_NAME_PREFIX = SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "I_";
	
	/**
	 * 是否启用 {@link Index}、 {@link NeedIndex} 与 {@link ReverseIndex} 支持，true为启用
	 */
	private boolean enable = true;
	
	/** 表达式取得该对象的路径 */
	private final String path = PATH;
	
	/** 默认安全用户 */
	private String defaultSecurityUser = Apps.getAppName();
	
	/** 是否在记录日志时记录原文，为true则需要记录 */
	private boolean logOriginal;
	
	/** 使用的索引管理器的引用名称 */
	private String indexManagerRefName = BOOT_CREATE_BEAN_NAME_PREFIX + "IndexManager";
	
	/** {@link com.yiji.common.security.index.IndexManager} 的来源环境 */
	private String indexManagerEnv = "domain";
	
	/** 安全用户的引用名 */
	private String securityUserRef;
	
	/**
	 * 是否开启索引所有被{@link Sensitive} 注解了的方法，为true是开启
	 */
	private boolean indexAllSensitiveMethod = true;
	
	/** 在反向索引时，如果没有对应的索引值是否忽略，为true表示忽略，默认不忽略 */
	private boolean reverseIndexeIgnoreNoIndex;
	
	/** 是否启用本地缓存；设置为true，将安全索引结果写入redis缓存，如果不启用，直接使用安全中心提供的redis */
	@AutoConfig(PATH + ".enableLocalCache")
	private boolean enableLocalCache = false;
	
	/** 是否启用索引/解密过程中的日志 */
	@AutoConfig(PATH + ".enableIndexLog")
	private boolean enableIndexLog = false;
	
	/**
	 * 判定是否启用 {@link Index}、 {@link NeedIndex} 与 {@link ReverseIndex} 支持。
	 * @return true为启用。
	 */
	public boolean isEnable() {
		return this.enable;
	}
	
	/**
	 * 设置是否启用 {@link Index}、 {@link NeedIndex} 与 {@link ReverseIndex} 支持。
	 * @param enable true为启用。
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	/**
	 * 得到表达式取得该对象的路径。
	 * @return 表达式取得该对象的路径。
	 */
	public String getPath() {
		return this.path;
	}
	
	/**
	 * 得到默认安全用户。
	 * @return 默认安全用户。
	 */
	public String getDefaultSecurityUser() {
		return this.defaultSecurityUser;
	}
	
	/**
	 * 设置默认安全用户。
	 * @param defaultSecurityUser 默认安全用户。
	 */
	public void setDefaultSecurityUser(String defaultSecurityUser) {
		this.defaultSecurityUser = defaultSecurityUser;
	}
	
	/**
	 * 得到判定是否在记录日志时记录原文。
	 * @return 为true则需要记录。如果没有调用 {@link #setLogOriginal(boolean)} 作过修改，默认返回 false 。
	 */
	public boolean isLogOriginal() {
		return this.logOriginal;
	}
	
	/**
	 * 设置是否在记录日志时记录原文。
	 * @param logOriginal 为true则需要记录。
	 */
	public void setLogOriginal(boolean logOriginal) {
		this.logOriginal = logOriginal;
	}
	
	/**
	 * 得到使用的索引管理器的引用名称。
	 * @return 使用的索引管理器的引用名称。
	 */
	public String getIndexManagerRefName() {
		return this.indexManagerRefName;
	}
	
	/**
	 * 设置使用的索引管理器的引用名称。
	 * @param indexManagerRefName 使用的索引管理器的引用名称。
	 */
	public void setIndexManagerRefName(String indexManagerRefName) {
		this.indexManagerRefName = indexManagerRefName;
	}
	
	/**
	 * 得到{@link com.yiji.common.security.index.IndexManager} 的来源环境。
	 * @return {@link com.yiji.common.security.index.IndexManager} 的来源环境。
	 */
	public String getIndexManagerEnv() {
		return this.indexManagerEnv;
	}
	
	/**
	 * 设置{@link com.yiji.common.security.index.IndexManager} 的来源环境。
	 * @param indexManagerEnv {@link com.yiji.common.security.index.IndexManager} 的来源环境。
	 */
	public void setIndexManagerEnv(String indexManagerEnv) {
		this.indexManagerEnv = indexManagerEnv;
	}
	
	/**
	 * 得到安全用户的引用名。
	 * @return 安全用户的引用名。
	 */
	public String getSecurityUserRef() {
		return this.securityUserRef;
	}
	
	/**
	 * 设置安全用户的引用名。
	 * @param securityUserRef 安全用户的引用名。
	 */
	public void setSecurityUserRef(String securityUserRef) {
		this.securityUserRef = securityUserRef;
	}
	
	/**
	 * 得到判定是否开启索引所有被{@link Sensitive} 注解了的方法。
	 * @return 为true是开启 。如果没有调用 {@link #setIndexAllSensitiveMethod(boolean)} 作过修改，则默认返回 true 。
	 */
	public boolean isIndexAllSensitiveMethod() {
		return this.indexAllSensitiveMethod;
	}
	
	/**
	 * 设置是否开启索引所有被{@link Sensitive} 注解了的方法。
	 * @param indexAllSensitiveMethod 为true是开启 。
	 */
	public void setIndexAllSensitiveMethod(boolean indexAllSensitiveMethod) {
		this.indexAllSensitiveMethod = indexAllSensitiveMethod;
	}
	
	/**
	 * 判定在反向索引时，如果没有对应的索引值是否忽略。
	 * @return 为true表示忽略 。
	 */
	public boolean isReverseIndexeIgnoreNoIndex() {
		return this.reverseIndexeIgnoreNoIndex;
	}
	
	/**
	 * 设置在反向索引时，如果没有对应的索引值是否忽略。
	 * @param reverseIndexeIgnoreNoIndex 为true表示忽略 。
	 */
	public void setReverseIndexeIgnoreNoIndex(boolean reverseIndexeIgnoreNoIndex) {
		this.reverseIndexeIgnoreNoIndex = reverseIndexeIgnoreNoIndex;
	}
	
	/**
	 * 设置是否启用本地缓存；设置为true，将安全索引结果写入redis缓存，如果不启用，直接使用安全中心提供的redis
	 *
	 * @return
	 */
	public boolean isEnableLocalCache() {
		return enableLocalCache;
	}
	
	/**
	 * 设置是否启用本地缓存；设置为true，将安全索引结果写入redis缓存，如果不启用，直接使用安全中心提供的redis
	 * @param enableLocalCache 为true标识启用本地缓存
	 */
	public void setEnableLocalCache(boolean enableLocalCache) {
		this.enableLocalCache = enableLocalCache;
	}
	
	/**
	 * 是否启用索引/解密过程中的日志
	 *
	 * @return
	 */
	public boolean isEnableIndexLog() {
		return enableIndexLog;
	}
	
	/**
	 * 是否启用索引/解密过程中的日志
	 *
	 * @param enableIndexLog
	 */
	public void setEnableIndexLog(boolean enableIndexLog) {
		this.enableIndexLog = enableIndexLog;
	}
}
