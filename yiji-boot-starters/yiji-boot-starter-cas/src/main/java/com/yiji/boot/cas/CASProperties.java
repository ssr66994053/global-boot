/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 *
 * 修订记录:
 * shuijing@yiji.com 2016-07-07 15:41 创建
 *
 */
package com.yiji.boot.cas;

import com.google.common.collect.Lists;
import com.yiji.boot.core.Apps;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * @author shuijing@yiji.com
 */
@ConfigurationProperties("yiji.cas")
public class CASProperties {
	
	/**
	 * 是否启用cas组件
	 */
	private boolean enable = true;
	/**
	 * default permissions to applied to authenticated user 默认权限
	 */
	private String defaultPermissions;
	/**
	 * default roles to applied to authenticated user 默认角色
	 */
	private String defaultRoles;
	/**
	 * 客户端应用服务的域名和端口
	 */
	private String clientServerName;
	
	private String loginUrl;
	/**
	 * 单点登录服务器域名和端口
	 */
	private String casServer;
	/**
	 * 服务端地址前缀
	 */
	private String serverUrlPrefix;
	/**
	 * 登录成功url
	 */
	private String successUrl;
	
	/**
	 * 默认不进行过滤的地址
	 */
	public static final String FILTER_CHAIN_DEF = "    /mgt/**           = anon\n" + "    /boss/**	         = anon\n"
													+ "    /shiro-cas        = casFilter\n"
													+ "    /login	         = anon\n" + "    /css/**           = anon\n"
													+ "    /js/**            = anon\n"
													+ "    /img/**	         = anon\n"
													+ "    /images/**        = anon\n"
													+ "    /resources/**     = anon\n";
	public static final String FILTER_AUTHC_DEF = "/**=authc";
	/**
	 * 不需要shiro授权的系统
	 * @return
	 */
	public static final String EXCLUDED_AUTHC_APP[] = { "openapi", "cashier"};
	/**
	 * 对应shiro.ini中的[urls]标签，,FILTER_CHAIN_DEF默认的排除的权限就不需要添加了,注意顺序，格式如：
	 *
	 * <pre>
	 * yiji.cas.excludeUrls[0]./main.html = anon
	 * yiji.cas.excludeUrls[1]./** = anon
	 * </pre>
	 */
	private List<Map<String, String>> excludeUrls = Lists.newLinkedList();
	
	/**
	 * 需要拦截的路径
	 */
	private String urlPattern = "/*";
	
	/**
	 * redisManager过期时间
	 */
	private Integer redisManagerExpireTime = 1800;
	
	private String namespace = Apps.getAppName() + ".st.cache";
	
	public static String[] getExcludedAuthcApp() {
		return EXCLUDED_AUTHC_APP;
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	public String getClientServerName() {
		return clientServerName;
	}
	
	public String getLoginUrl() {
		return loginUrl;
	}
	
	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}
	
	public void setClientServerName(String clientServerName) {
		this.clientServerName = clientServerName;
	}
	
	public Integer getRedisManagerExpireTime() {
		return redisManagerExpireTime;
	}
	
	public void setRedisManagerExpireTime(Integer redisManagerExpireTime) {
		this.redisManagerExpireTime = redisManagerExpireTime;
	}

	public String getServerUrlPrefix() {
		return serverUrlPrefix;
	}

	public void setServerUrlPrefix(String serverUrlPrefix) {
		this.serverUrlPrefix = serverUrlPrefix;
	}

	public String getUrlPattern() {
		return urlPattern;
	}
	
	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}
	
	public List<Map<String, String>> getExcludeUrls() {
		return excludeUrls;
	}
	
	public void setExcludeUrls(List<Map<String, String>> excludeUrls) {
		this.excludeUrls = excludeUrls;
	}
	
	public String getSuccessUrl() {
		return successUrl;
	}
	
	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}
	
	public String getCasServer() {
		return casServer;
	}
	
	public void setCasServer(String casServer) {
		this.casServer = casServer;
	}
	
	public String getDefaultPermissions() {
		return defaultPermissions;
	}
	
	public void setDefaultPermissions(String defaultPermissions) {
		this.defaultPermissions = defaultPermissions;
	}
	
	public String getDefaultRoles() {
		return defaultRoles;
	}
	
	public void setDefaultRoles(String defaultRoles) {
		this.defaultRoles = defaultRoles;
	}
	
	public boolean isEnable() {
		return enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
}
