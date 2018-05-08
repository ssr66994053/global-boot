/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-10-29 21:54 创建
 *
 */
package com.global.boot.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.common.store.DataStore;
import com.alibaba.dubbo.registry.Registry;
import com.alibaba.dubbo.registry.support.AbstractRegistryFactory;
import com.alibaba.dubbo.remoting.exchange.ExchangeServer;
import com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yjf.common.concurrent.MonitoredThreadPoolExecutor;
import com.yjf.common.lang.exception.Exceptions;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author qiubo@yiji.com
 */
public class DubboHealthIndicator extends AbstractHealthIndicator {
	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		try {
			checkRegistry(builder);
			
			checkExchangeServer(builder);
			
			checkThreadpool(builder);
		} catch (Exception e) {
			//do nothing
		}
	}
	
	private void checkThreadpool(Health.Builder builder) {
		DataStore dataStore = ExtensionLoader.getExtensionLoader(DataStore.class).getDefaultExtension();
		Map<String, Object> executors = dataStore.get(Constants.EXECUTOR_SERVICE_COMPONENT_KEY);
		if (executors == null || executors.isEmpty()) {
			builder.up().withDetail("executors", "no executors found");
			return;
		} else {
			List<Map<String, Object>> result = Lists.newArrayList();
			for (Map.Entry<String, Object> entry : executors.entrySet()) {
				ExecutorService executor = (ExecutorService) entry.getValue();
				if (executor != null && executor instanceof MonitoredThreadPoolExecutor) {
					MonitoredThreadPoolExecutor tp = (MonitoredThreadPoolExecutor) executor;
					int submittedCount = tp.getSubmittedCount();
					int maximumPoolSize = tp.getMaximumPoolSize();
					int remainingCapacity = tp.getQueue().remainingCapacity();
					Map<String, Object> serverResult = Maps.newHashMap();
					serverResult.put("submittedCount", submittedCount);
					serverResult.put("maximumPoolSize", maximumPoolSize);
					serverResult.put("remainingCapacity", remainingCapacity);
					result.add(serverResult);
					if (submittedCount >= maximumPoolSize) {
						builder.down().withDetail("executors", result);
						throw Exceptions.newRuntimeExceptionWithoutStackTrace("executors is unhealthy");
					}
				}
			}
			builder.up().withDetail("executors", result);
		}
	}
	
	private void checkExchangeServer(Health.Builder builder) {
		Collection<ExchangeServer> servers = DubboProtocol.getDubboProtocol().getServers();
		if (servers == null || servers.size() == 0) {
			builder.up();
		} else {
			List<Map<String, Object>> result = Lists.newArrayList();
			for (ExchangeServer server : servers) {
				Map<String, Object> serverResult = Maps.newHashMap();
				boolean isBound = server.isBound();
				serverResult.put("isBound", isBound);
				if (!isBound) {
					builder.down().withDetail("server", "server is unbound");
					throw Exceptions.newRuntimeExceptionWithoutStackTrace("server is unbound");
				}
				serverResult.put("port", server.getLocalAddress().getPort());
				result.add(serverResult);
			}
			builder.up().withDetail("server", result);
		}
	}
	
	private void checkRegistry(Health.Builder builder) {
		Collection<Registry> regsitries = AbstractRegistryFactory.getRegistries();
		if (regsitries.size() == 0) {
			builder.up().withDetail("regsitries", "no registry found");
		} else {
			List<Map<String, String>> result = Lists.newArrayList();
			for (Registry registry : regsitries) {
				boolean available = registry.isAvailable();
				Map<String, String> registryInfo = Maps.newHashMapWithExpectedSize(2);
				registryInfo.put("address", registry.getUrl().getAddress());
				registryInfo.put("available", Boolean.toString(available));
				result.add(registryInfo);
				if (!available) {
					builder.down().withDetail("regsitries", result);
					throw Exceptions.newRuntimeExceptionWithoutStackTrace(" registry isn't avaliable");
				}
			}
			builder.up().withDetail("regsitries", result);
		}
		
	}
}
