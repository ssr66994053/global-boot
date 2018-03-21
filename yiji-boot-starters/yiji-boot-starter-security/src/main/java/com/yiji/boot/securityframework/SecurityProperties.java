/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * Agrael 2015年9月8日 上午11:38:18 创建
 */
package com.yiji.boot.securityframework;

import com.yiji.boot.core.AutoConfigurationProperties;
import com.yiji.common.security.annotation.Protected;
import com.yiji.common.security.bean.JavaBeanSyphon;
import com.yiji.common.security.bean.Syphon;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 安全框架核心功能的配置属性信息。 {@link com.yiji.common.security.SecurityManager} 的来源环境默认支持如下环境：
 * <ul>
 * <li>local：使用本地环境提供 <code>com.yiji.common.security.referenceimplements.SecurityConfig</code> 来完成。需要在容器中提供
 * <code>com.yiji.boot.securityframework.environment.local.SecurityConfigRepository</code> 的实例来提供
 * <code>com.yiji.common.security.referenceimplements.SecurityConfig</code> 。如果容器中没有
 * <code>com.yiji.boot.securityframework.environment.local.SecurityConfigRepository</code> 的实例则会报错。</li>
 * <li>ri：使用参考实现来提供 {@link com.yiji.common.security.SecurityManager} （beanName为 <code>yijiBoot_SF_SecurityManager</code>
 * ），通过使用 <tt>beanName</tt>： <code>yijiBoot_SF_SecurityConfigs</code>从容器中获得对应的
 * <code>java.util.Map<java.lang.String, com.yiji.common.security.referenceimplements.SecurityConfig></code>
 * 配置相关的安全用户信息。这也是默认的配置</li>
 * <li>ref：直接引用环境中已经存在或者已经配置的 {@link com.yiji.common.security.SecurityManager} 实例。如果为该环境，则需要指定
 * {@link #setSecurityManagerRefName(String)} 。</li>
 * </ul>
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
@ConfigurationProperties(SecurityProperties.PATH)
public class SecurityProperties implements AutoConfigurationProperties, Serializable {
	
	private static final long serialVersionUID = -9148080789381041829L;
	
	/** boot环境中创建带name的bean的name前缀 */
	public static final String BOOT_CREATE_BEAN_NAME_PREFIX = "yijiBoot_SF_";
	
	/** 表达式取得该类实例的路径 */
	public static final String PATH = "yiji.security";
	
	/** 是否启用安全框架核心的功能，true为启用 */
	private boolean enable = true;
	
	/** 表达式取得该对象的路径 */
	private final String path = PATH;
	
	/** 使用的安全管理器的引用名称 */
	private String securityManagerRefName = BOOT_CREATE_BEAN_NAME_PREFIX + "SecurityManager";
	
	/** {@link com.yiji.common.security.SecurityManager} 的来源环境 */
	private String securityManagerEnv = "ri";
	
	/** 启用 {@link Syphon} 支持的属性 */
	private SyphonProperties syphonProperties = new SyphonProperties();
	
	/**
	 * 判定是否启用安全框架核心的功能。
	 * @return true为启用。
	 */
	public boolean isEnable() {
		return this.enable;
	}
	
	/**
	 * 设置是否启用安全框架核心的功能。
	 * @param enable true为启用。
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	/**
	 * 得到使用的安全管理器的引用名称。
	 * @return 使用的安全管理器的引用名称。如果没有调用 {@link #setSecurityManagerRefName(String)} 作过修改，则返回默认值
	 * <code>yijiBoot_SF_SecurityManager</code>。
	 */
	public String getSecurityManagerRefName() {
		return this.securityManagerRefName;
	}
	
	/**
	 * 设置使用的安全管理器的引用名称。
	 * @param securityManagerRefName 使用的安全管理器的引用名称。
	 */
	public void setSecurityManagerRefName(String securityManagerRefName) {
		this.securityManagerRefName = securityManagerRefName;
	}
	
	/**
	 * 得到表达式取得该对象的路径。
	 * @return 表达式取得该对象的路径。
	 */
	public String getPath() {
		return this.path;
	}
	
	/**
	 * 得到{@link com.yiji.common.security.SecurityManager} 的来源环境。
	 * @return {@link com.yiji.common.security.SecurityManager} 的来源环境。
	 */
	public String getSecurityManagerEnv() {
		return this.securityManagerEnv;
	}
	
	/**
	 * 设置{@link com.yiji.common.security.SecurityManager} 的来源环境。
	 * @param securityManagerEnv {@link com.yiji.common.security.SecurityManager} 的来源环境。
	 */
	public void setSecurityManagerEnv(String securityManagerEnv) {
		this.securityManagerEnv = securityManagerEnv;
	}
	
	/**
	 * 得到启用 {@link Syphon} 支持的属性。
	 * @return 启用 {@link Syphon} 支持的属性。
	 */
	@NotNull
	public SyphonProperties getSyphonProperties() {
		return this.syphonProperties;
	}
	
	/**
	 * 设置启用 {@link Syphon} 支持的属性。
	 * @param syphonProperties 启用 {@link Syphon} 支持的属性。
	 */
	public void setSyphonProperties(SyphonProperties syphonProperties) {
		this.syphonProperties = syphonProperties;
	}
	
	/**
	 * 启用 {@link Syphon} 支持的属性。该属性对应 {@link JavaBeanSyphon} 设置的默认属性。
	 *
	 *
	 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
	 *
	 */
	public static class SyphonProperties implements AutoConfigurationProperties, Serializable {
		
		private static final long serialVersionUID = -3814444065440299850L;
		
		/** 表达式取得该类实例的路径 */
		public static final String PATH = SecurityProperties.PATH + ".syphonProperties";
		
		/** 是否启用 {@link Syphon} ，true为启用 */
		private boolean enable = true;
		
		/** 表达式取得该对象的路径 */
		private final String path = PATH;
		
		/** {@link JavaBeanSyphon} 是否支持 {@link Protected} 注解，true表示支持 */
		private boolean supportProtected = true;
		
		/**
		 * 判定是否启用 {@link Syphon} 。
		 * @return true为启用。
		 */
		public boolean isEnable() {
			return this.enable;
		}
		
		/**
		 * 设置是否启用 {@link Syphon} 。
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
		 * 判定{@link Syphon} 是否支持 {@link Protected} 注解。
		 * @return true表示支持。
		 */
		public boolean isSupportProtected() {
			return this.supportProtected;
		}
		
		/**
		 * 设置{@link Syphon} 是否支持 {@link Protected} 注解。
		 * @param supportProtected true表示支持。
		 */
		public void setSupportProtected(boolean supportProtected) {
			this.supportProtected = supportProtected;
		}
		
	}
	
}
