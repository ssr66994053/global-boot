/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * zhouxi@yiji.com 2016-01-12 09:56 创建
 *
 */
package com.global.boot.actuator.autoexport;

import com.global.boot.actuator.ActuatorAutoConfiguration;
import com.global.boot.core.Apps;
import com.global.boot.core.CommonProperties;
import com.global.common.lang.ip.IPUtil;
import com.global.common.util.ShutdownHooks;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;

import static org.apache.zookeeper.Watcher.Event.KeeperState.Expired;

/**
 * @author zhouxi@yiji.com 为了统一，我们借助dubbo带来的能力，来做服务发现
 */
@Configuration
@AutoConfigureAfter({ ActuatorAutoConfiguration.class })
public class ActuatorServiceExportAutoConfiguration implements ApplicationListener<ApplicationReadyEvent> {
	private static final Logger logger = LoggerFactory.getLogger(ActuatorServiceExportAutoConfiguration.class);
	
	private CommonProperties commonProperties;
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		final RetryTemplate retryTemplate = new RetryTemplate();
		final SimpleRetryPolicy policy = new SimpleRetryPolicy(10, Collections.singletonMap(Exception.class, true));
		FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
		fixedBackOffPolicy.setBackOffPeriod(100);
		retryTemplate.setRetryPolicy(policy);
		retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
		commonProperties = Apps.buildProperties(CommonProperties.class);
		register(retryTemplate);
	}
	
	private void register(RetryTemplate retryTemplate) {
		String path = getPath();
		try {
			final RetryCallback<ZooKeeper, Exception> zkClinetRetryCallback = context -> {
				ZooKeeper zk = new ZooKeeper(commonProperties.getZkUrl(), 90000, event -> {
					if (event.getState() == Expired) {
						register(retryTemplate);
					}
				});
				return zk;
			};
			ZooKeeper zk = retryTemplate.execute(zkClinetRetryCallback);
			ShutdownHooks.addShutdownHook(() -> {
				if (zk != null) {
					try {
						zk.close();
					} catch (InterruptedException e) {
						logger.warn("Actuator取消注册时关闭zk连接失败",e);
					}
				}
			}, "Actuator取消注册");
			final RetryCallback<Object, Exception> retryCallback = context -> {
				ensureParentNode(zk);
				zk.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
				return null;
			};
			retryTemplate.execute(retryCallback);
		} catch (Exception e) {
			logger.warn("Actuator注册失败.", e);
		}
	}
	
	private void ensureParentNode(ZooKeeper zk) throws KeeperException, InterruptedException {
		if (zk.exists("/dubbo", false) == null) {
			zk.create("/dubbo", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		if (zk.exists("/dubbo/" + Apps.getAppName(), false) == null) {
			zk.create("/dubbo/" + Apps.getAppName(), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		if (zk.exists("/dubbo/" + Apps.getAppName() + "/mgt", false) == null) {
			zk.create("/dubbo/" + Apps.getAppName() + "/mgt", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
	}
	
	private String getPath() {
		String path = "/dubbo/" + Apps.getAppName() + "/mgt/";
		String subPath = "http://"	+ IPUtil.getFirstNoLoopbackIPV4Address() + ":" + Apps.getHttpPort() + "/"
							+ Apps.getAppName() + "?application=" + Apps.getAppName() + "&category=mgt";
		path += encode(subPath);
		return path;
	}
	
	private static String encode(String value) {
		if (value == null || value.length() == 0) {
			return "";
		}
		try {
			return URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
