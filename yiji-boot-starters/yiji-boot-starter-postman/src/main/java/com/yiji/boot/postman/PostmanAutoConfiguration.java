/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2015-09-22 14:59 创建
 *
 */
package com.yiji.boot.postman;

import com.google.common.collect.Lists;
import com.yiji.boot.core.AppConfigException;
import com.yiji.framework.hera.client.exception.HeraException;
import com.yiji.framework.hera.client.listener.Event;
import com.yiji.framework.hera.client.listener.ValueTrigger;
import com.yiji.postman.PostmanClient;
import com.yjf.common.concurrent.MonitoredThreadPool;
import com.yjf.common.env.Env;
import com.yjf.common.net.HttpRequest;
import com.yjf.common.portrait.model.HTTPEndpoint;
import com.yjf.common.portrait.model.IOResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.HttpURLConnection;
import java.util.List;

/**
 * @author daidai@yiji.com
 */
@Configuration
@ConditionalOnProperty(value = "yiji.postman.enable", matchIfMissing = true)
@EnableConfigurationProperties({ PostmanProperties.class })
public class PostmanAutoConfiguration implements ValueTrigger, IOResource<HTTPEndpoint> {
	private static final Logger logger = LoggerFactory.getLogger(PostmanAutoConfiguration.class);
	
	@Autowired
	private PostmanProperties postmanProperties;
	private MonitoredThreadPool postmanThreadPool;
	
	@Bean
	public PostmanClient postmanClient() {
		checkUrl();
		PostmanClient postmanClient = new PostmanClient(postmanProperties.getServiceUrl(),
			postmanProperties.getAppkey(), postmanProperties.getAppsecret(), Env.getEnv(), postmanThreadPool());
		return postmanClient;
	}
	
	private void checkUrl() {
		String url = postmanProperties.getHealthCheckUrl();
		int code;
		try {
			code = HttpRequest.head(url).connectTimeout(10000).readTimeout(5000).trustAllCerts().trustAllHosts().code();
		} catch (Exception e) {
			throw new AppConfigException("postman服务端地址" + url + "访问失败", e);
		}
		if (code != HttpURLConnection.HTTP_OK) {
			throw new AppConfigException("postman服务端地址" + url + "访问失败,statusCode=" + code);
		}
	}
	
	@Bean
	public MonitoredThreadPool postmanThreadPool() {
		MonitoredThreadPool monitoredThreadPool = new MonitoredThreadPool();
		monitoredThreadPool.setCorePoolSize(1);
		monitoredThreadPool.setMaxPoolSize(postmanProperties.getMaxPoolSize());
		monitoredThreadPool.setQueueCapacity(500);
		monitoredThreadPool.setThreadNamePrefix("postman-client-");
		postmanThreadPool = monitoredThreadPool;
		return monitoredThreadPool;
	}
	
	//	@Bean
	public PostmanHealthIndicator postmanHealthIndicator() {
		return new PostmanHealthIndicator();
	}
	
	@Override
	public void onChange(Event event) throws HeraException {
		event.ifPresent("yiji.postman.maxPoolSize", maxPoolSize -> {
			if (postmanThreadPool != null) {
				postmanThreadPool.setMaxPoolSize(Integer.valueOf(maxPoolSize));
				logger.info("hera修改postman配置maxPoolSize:{}", maxPoolSize);
			}
			
		});
	}
	
	@Override
	public List<HTTPEndpoint> endpoints() {
		HTTPEndpoint httpEndpoint = new HTTPEndpoint();
		httpEndpoint.setUrl(postmanProperties.getServiceUrl());
		httpEndpoint.setName("postman");
		return Lists.newArrayList(httpEndpoint);
	}
}
