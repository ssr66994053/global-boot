/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-09-15 15:07 创建
 *
 */
package com.yiji.boot.fastdfs;

import com.google.common.collect.Lists;
import com.yiji.boot.core.metrics.GenericKeyedObjectPoolMetrics;
import com.yiji.framework.fastdfs.FastdfsClient;
import com.yiji.framework.fastdfs.FastdfsClientConfig;
import com.yiji.framework.fastdfs.FastdfsClientFactory;
import com.yjf.common.portrait.model.IOResource;
import com.yjf.common.portrait.model.TCPEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.List;

/**
 * @author yanglie@yiji.com
 */
@Configuration
@ConditionalOnProperty(value = FastdfsProperties.PREFIX + ".enable", matchIfMissing = true)
@EnableConfigurationProperties({ FastdfsProperties.class })
public class FastdfsAutoConfiguration implements IOResource<TCPEndpoint> {
	
	private static final String TRACKER_OBJECT_NAME = "org.apache.commons.pool2:type=GenericKeyedObjectPool,name=fastdfs.tracker";
	private static final String STORAGE_OBJECT_NAME = "org.apache.commons.pool2:type=GenericKeyedObjectPool,name=fastdfs.storage";
	
	private static final String TRACKER_JMX_PREFIX = "fastdfs.tracker";
	private static final String STORAGE_JMX_PREFIX = "fastdfs.storage";
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private FastdfsProperties fastdfsProperties;
	
	@Bean(destroyMethod = "close")
	public FastdfsClient fastdfsClient(FastdfsProperties fastdfsProperties) {
		FastdfsClientConfig clientConfig = new FastdfsClientConfig();
		clientConfig.setTrackerAddress(fastdfsProperties.getTrackerAddress());
		clientConfig.setDownloadHost(fastdfsProperties.getDownloadHost());
		clientConfig.setEncryptedDownloadHost(fastdfsProperties.getEncryptedDownloadHost());
		clientConfig.setEncryptedGroups(fastdfsProperties.getEncryptedGroups());
		clientConfig.setConnectTimeout(fastdfsProperties.getConnectTimeout() * 1000);
		clientConfig.setNetworkTimeout(fastdfsProperties.getNetworkTimeOut() * 1000);
		clientConfig.setMaxTotal(fastdfsProperties.getPool().getMaxTotal());
		clientConfig.setMaxIdlePerKey(fastdfsProperties.getPool().getMaxIdlePerNode());
		clientConfig.setMaxTotalPerKey(fastdfsProperties.getPool().getMaxTotalPerNode());
		clientConfig.setTrackerClientJmxNamePrefix(TRACKER_JMX_PREFIX);
		clientConfig.setStorageClientJmxNamePrefix(STORAGE_JMX_PREFIX);
		return FastdfsClientFactory.getFastdfsClient(clientConfig);
	}
	
//	@Bean
	public GenericKeyedObjectPoolMetrics fastdfsTrackerMetrics() {
		final ObjectName objectName;
		try {
			objectName = new ObjectName(TRACKER_OBJECT_NAME);
		} catch (MalformedObjectNameException e) {
			logger.error("", e);
			throw new IllegalArgumentException(e);
		}
		return new GenericKeyedObjectPoolMetrics(objectName, TRACKER_JMX_PREFIX);
	}
	
//	@Bean
	public GenericKeyedObjectPoolMetrics fastdfsStorageMetrics() {
		final ObjectName objectName;
		try {
			objectName = new ObjectName(STORAGE_OBJECT_NAME);
		} catch (MalformedObjectNameException e) {
			logger.error("", e);
			throw new IllegalArgumentException(e);
		}
		return new GenericKeyedObjectPoolMetrics(objectName, STORAGE_JMX_PREFIX);
	}
	
	@Bean
	public FastdfsHealthIndicator fastdfsHealthIndicator() {
		return new FastdfsHealthIndicator();
	}
	
	@Override
	public List<TCPEndpoint> endpoints() {
		TCPEndpoint tcpEndpoint = new TCPEndpoint();
		String address = fastdfsProperties.getTrackerAddress();
		tcpEndpoint.setName("fastdfs");
		tcpEndpoint.parseIpAndPort(address);
		return Lists.newArrayList(tcpEndpoint);
	}
}
