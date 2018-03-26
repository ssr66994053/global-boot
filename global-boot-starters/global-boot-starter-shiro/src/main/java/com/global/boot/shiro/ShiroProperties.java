/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-20 16:14 创建
 *
 */
package com.global.boot.shiro;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author husheng@yiji.com
 */
@ConfigurationProperties(ShiroProperties.PREFIX)
public class ShiroProperties implements InitializingBean {
	public static final String PREFIX = "yiji.shiro";
	
	/**
	 * 是否启用shiro
	 */
	private boolean enable = true;
	
	/**
	 * 登录页面链接
	 */
	private String loginUrl = "/login.html";
	
	/**
	 * 没有权限时跳转的链接
	 */
	private String unauthorizedUrl = "/unauthorized.html";
	
	/**
	 * 登录成功后的链接
	 */
	private String successUrl = "/success.html";
	
	/**
	 * 需要shiro拦截的链接
	 */
	private String urlPattern = "/*";
	
	/**
	 * 对应shiro.ini中的[urls]标签，注意顺序，格式如：
	 * 
	 * <pre>
     * yiji.shiro.urls[0]./shiro/** = authc
     * yiji.shiro.urls[1]./** = anon
     * </pre>
	 */
	private List<Map<String, String>> urls = Lists.newLinkedList();
	
	/**
	 * 自定义Filter列表，对应shiro.ini中的[filters]标签，格式如：
	 * 
	 * <pre>
     *  yiji.shiro.filters.authc=com.yiji.neverstopfront.web.shiro.CaptchaFormAuthenticationFilter
     *  yiji.shiro.filters.admin=com.yiji.neverstopfront.web.shiro.AdminAuthorizationFilter
     *  houseProperty=com.yiji.neverstopfront.web.shiro.ServiceTypeAuthorizationFilter
     *  houseProperty.serviceType=HOUSE_PROPERTY
     *  installment=com.yiji.neverstopfront.web.shiro.ServiceTypeAuthorizationFilter
     *  installment.serviceType=INSTALLMENT
     *  </pre>
	 * <p>
	 * </ul>
	 */
	private LinkedHashMap<String, String> filters = Maps.newLinkedHashMap();
	
	@Override
	public void afterPropertiesSet() throws Exception {
	}
	
	public boolean isEnable() {
		return enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	public String getLoginUrl() {
		return loginUrl;
	}
	
	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}
	
	public String getUnauthorizedUrl() {
		return unauthorizedUrl;
	}
	
	public void setUnauthorizedUrl(String unauthorizedUrl) {
		this.unauthorizedUrl = unauthorizedUrl;
	}
	
	public String getSuccessUrl() {
		return successUrl;
	}
	
	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}
	
	public String getUrlPattern() {
		return urlPattern;
	}
	
	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}
	
	public List<Map<String, String>> getUrls() {
		return urls;
	}
	
	public void setUrls(List<Map<String, String>> urls) {
		this.urls = urls;
	}
	
	public Map<String, String> getFilters() {
		return filters;
	}
	
	public void setFilters(LinkedHashMap<String, String> filters) {
		this.filters = filters;
	}
}
