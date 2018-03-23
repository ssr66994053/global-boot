/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-04-23 11:22 创建
 */
package com.global.boot.actuator.endpoint;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yjf.common.portrait.model.Endpoint;
import com.yjf.common.portrait.model.IOResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;

/**
 * @author qiubo@yiji.com
 */
@ConfigurationProperties(prefix = "endpoints.io")
public class IOEndpoint extends AbstractEndpoint<Map<String, List<Endpoint>>> {
	@Autowired
	private ApplicationContext applicationContext;
	private volatile Map<String, List<Endpoint>> endpoints;
	
	public IOEndpoint() {
		super("io");
	}
	
	@Override
	public synchronized Map<String, List<Endpoint>> invoke() {
		if (this.endpoints != null) {
			return this.endpoints;
		}
		Map<String, List<Endpoint>> endpoints = Maps.newHashMap();
		Map<String, IOResource> beansOfType = applicationContext.getBeansOfType(IOResource.class);
		for (IOResource ioResource : beansOfType.values()) {
			List<Endpoint> eps = ioResource.endpoints();
			if (eps != null) {
				for (Endpoint ep : eps) {
					String key = ep.getClass().getSimpleName().replace("Endpoint", "");
					if (endpoints.containsKey(key)) {
						List<Endpoint> list = endpoints.get(key);
						if (!list.contains(ep)) {
							list.add(ep);
						}
					} else {
						endpoints.put(key, Lists.newArrayList(ep));
					}
				}
			}
		}
		this.endpoints = endpoints;
		return endpoints;
	}
}
