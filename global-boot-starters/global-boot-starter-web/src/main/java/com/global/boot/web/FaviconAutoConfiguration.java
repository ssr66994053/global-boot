/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-16 16:52 创建
 *
 */
package com.global.boot.web;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.Collections;
import java.util.List;

/**
 * @author qiubo@yiji.com
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(value = "yiji.favicon.enable", matchIfMissing = true)
@EnableConfigurationProperties({ FaviconAutoConfiguration.FaviconProperties.class })
public class FaviconAutoConfiguration implements ResourceLoaderAware {
	
	@Autowired
	private FaviconProperties faviconProperties;
	
	private ResourceLoader resourceLoader;
	
	@Bean
	public SimpleUrlHandlerMapping faviconHandlerMapping() {
		SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
		mapping.setOrder(Integer.MIN_VALUE + 1);
		mapping.setUrlMap(Collections.singletonMap("**/favicon.ico", faviconRequestHandler()));
		return mapping;
	}
	
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	@Bean
	public ResourceHttpRequestHandler faviconRequestHandler() {
		ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
		requestHandler.setLocations(getLocations());
		return requestHandler;
	}
	
	private List<Resource> getLocations() {
		return Lists.newArrayList(this.resourceLoader.getResource("classpath:/META-INF/static/"));
	}
	
	@ConfigurationProperties("yiji.favicon")
	public static class FaviconProperties {
		/**
		 * 是否启用favicon
		 */
		private boolean enable = true;
		
		public boolean isEnable() {
			return enable;
		}
		
		public void setEnable(boolean enable) {
			this.enable = enable;
		}
	}
}
