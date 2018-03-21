/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-10-19 17:35 创建
 *
 */
package com.yiji.boot.web;

import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yanglie@yiji.com
 */
@ConfigurationProperties("yiji.web")
public class YijiWebProperties {
	/**
	 * 配置直接返回静态模板页面的映射 /index.htm->index,/test/test.htm->test/detail
	 */
	private String simplePageMap;
	
	/**
	 * 是否启用全局异常处理器,如果应用需要自定义MVC异常处理器,请禁用全局异常处理器,自定义自己的异常处理器.
	 */
	private boolean globalExceptionHandlerEnable = true;
	
	/**
	 * 是否启用hiddenHttpMethodFilter
	 * @see WebMvcAutoConfiguration#hiddenHttpMethodFilter()
	 */
	private boolean hiddenHttpMethodFilterEnable = false;
	
	/**
	 * 是否启用httpPutFormContentFilter
	 * @see WebMvcAutoConfiguration#httpPutFormContentFilter()
	 */
	private boolean HttpPutFormContentFilterEnable = false;
	
	/**
	 * 配置自定义欢迎页面,比如设置登录页面为欢迎页:login.html
	 */
	private String welcomeFile = null;
	
	/**
	 * http 缓存时间,-1=不设置,0=第二次请求需要和服务器协商,大于0=经过多少秒后才过期
	 */
	private int cacheMaxAge = -1;
	
	/**
	 * 在controller抛出异常时，是否escape exception message
	 */
	private boolean escapeExceptionMsg = true;
	
	public boolean isEscapeExceptionMsg() {
		return escapeExceptionMsg;
	}
	
	public void setEscapeExceptionMsg(boolean escapeExceptionMsg) {
		this.escapeExceptionMsg = escapeExceptionMsg;
	}
	
	public String getSimplePageMap() {
		return simplePageMap;
	}
	
	public void setSimplePageMap(String simplePageMap) {
		this.simplePageMap = simplePageMap;
	}
	
	public List<String> buildMappingUrlList() {
		List mappingUrlList = new ArrayList<>();
		if (simplePageMap == null) {
			return mappingUrlList;
		}
		String[] settingEntries = simplePageMap.split(",");
		for (String settingEntry : settingEntries) {
			String[] singleMapping = settingEntry.split("->");
			if (singleMapping == null || singleMapping.length != 2) {
				throw new RuntimeException("直接返回模板页面的配置格式不正确");
			}
			String key = singleMapping[0].trim();
			if (!key.startsWith("/")) {
				key = "/" + key;
			}
			mappingUrlList.add(key);
		}
		return mappingUrlList;
	}
	
	public Map<String, String> buildViewNameMap() {
		Map<String, String> viewNameMap = new HashMap<>();
		if (simplePageMap == null) {
			return viewNameMap;
		}
		String[] settingEntries = simplePageMap.split(",");
		for (String settingEntry : settingEntries) {
			String[] singleMapping = settingEntry.split("->");
			if (singleMapping == null || singleMapping.length != 2) {
				throw new RuntimeException("直接返回模板页面的配置格式不正确");
			}
			String key = singleMapping[0].trim();
			if (!key.startsWith("/")) {
				key = "/" + key;
			}
			viewNameMap.put(key, singleMapping[1].trim());
		}
		return viewNameMap;
	}
	
	public boolean isGlobalExceptionHandlerEnable() {
		return globalExceptionHandlerEnable;
	}
	
	public void setGlobalExceptionHandlerEnable(boolean globalExceptionHandlerEnable) {
		this.globalExceptionHandlerEnable = globalExceptionHandlerEnable;
	}
	
	public boolean isHiddenHttpMethodFilterEnable() {
		return hiddenHttpMethodFilterEnable;
	}
	
	public void setHiddenHttpMethodFilterEnable(boolean hiddenHttpMethodFilterEnable) {
		this.hiddenHttpMethodFilterEnable = hiddenHttpMethodFilterEnable;
	}
	
	public boolean isHttpPutFormContentFilterEnable() {
		return HttpPutFormContentFilterEnable;
	}
	
	public void setHttpPutFormContentFilterEnable(boolean httpPutFormContentFilterEnable) {
		HttpPutFormContentFilterEnable = httpPutFormContentFilterEnable;
	}
	
	public String getWelcomeFile() {
		return welcomeFile;
	}
	
	public void setWelcomeFile(String welcomeFile) {
		this.welcomeFile = welcomeFile;
	}
	
	public int getCacheMaxAge() {
		return cacheMaxAge;
	}
	
	public void setCacheMaxAge(int cacheMaxAge) {
		this.cacheMaxAge = cacheMaxAge;
	}
}
