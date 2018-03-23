/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * LiXiang 2016年2月18日 下午3:16:10 创建
 */
package com.yiji.boot.offlinelocks;

import com.google.common.collect.Lists;
import com.yiji.boot.core.Apps;
import com.yiji.boot.core.CommonProperties;
import com.yiji.concurrent.offlinelocks.LockManager;
import com.yiji.concurrent.offlinelocks.distributed.TimeoutCondition;
import com.yiji.concurrent.offlinelocks.distributed.zookeeperclient.ZooKeeperFactory;
import com.yiji.concurrent.offlinelocks.distributed.zookeeperclient.ZooKeeperFactoryImpl;
import com.yiji.concurrent.offlinelocks.distributed.zookeeperclient.ZooKeeperLockManager;
import com.yiji.concurrent.offlinelocks.vm.SimpleObjectVMLockManager;
import com.yjf.common.lang.ip.IPUtil;
import com.yjf.common.portrait.model.IOResource;
import com.yjf.common.portrait.model.TCPEndpoint;
import com.yjf.common.util.StringUtils;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * 离线锁插件的功能的自动配置。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
@Configuration
@EnableConfigurationProperties({ OfflineLocksProperties.class })
@ConditionalOnProperty(value = OfflineLocksProperties.PATH + ".enable", matchIfMissing = true)
public class OfflineLocksAutoConfiguration implements IOResource<TCPEndpoint> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OfflineLocksAutoConfiguration.class);
	
	@Autowired
	private OfflineLocksProperties offlineLocksProperties;
	
	@Autowired
	private CommonProperties commonProperties;
	
	@Bean
	public LockManager offlineLockManager() throws IOException {
		LOGGER.info("离线锁配置为:{}", offlineLocksProperties);
		if (isVm()) {
			return createVMLockManager();
		} else {
			return createZookeeperLockManager();
		}
	}
	
	private boolean isVm() {
		return offlineLocksProperties.getLockManagerEnv() != null
				&& offlineLocksProperties.getLockManagerEnv().equals("vm");
	}
	
	/**
	 * 在<code>vm</code>环境下创建一个名为<code>yijiBoot_OL_LockManager</code>的 {@link LockManager} 到容器。
	 * @return 创建的实例。
	 * @throws IOException 如果过程中发生I/O异常。
	 */
	private LockManager createVMLockManager() throws IOException {
		return new SimpleObjectVMLockManager(this.offlineLocksProperties.isFairLock());
	}
	
	/**
	 * 在<code>zookeeper</code>环境下创建一个名为<code>yijiBoot_OL_LockManager</code>的 {@link LockManager} 到容器。
	 * @return 创建的实例。
	 * @throws IOException 如果过程中发生I/O异常。
	 */
	private LockManager createZookeeperLockManager() throws IOException {
		ZooKeeperLockManager keeperLockManager;
		String rootDir = this.offlineLocksProperties.getRootDir();
		Charset encode;
		try {
			encode = this.offlineLocksProperties.getEncodeCharset() == null ? Charset.forName("UTF-8") : Charset
				.forName(this.offlineLocksProperties.getEncodeCharset());
		} catch (Exception e) {
			throw new LockConfigurationException("[" + this.offlineLocksProperties.mergePropertyPath("encodeCharset")
													+ "]属性不是正确的编码类型。",e);
		}
		if (StringUtils.isEmpty(this.commonProperties.getZkUrl())) {
			throw new LockConfigurationException("[yiji.common.zkUrl]没有配置。");
		}
		int sessionTimeout = (int) (this.offlineLocksProperties.getServerTimeout() <= 0 ? 60000
			: this.offlineLocksProperties.getServerTimeout());
		ZooKeeperFactory zooKeeperFactory = new ZooKeeperFactoryImpl(this.commonProperties.getZkUrl(), sessionTimeout,
			new Watcher() {
				
				private final Logger logger = LoggerFactory.getLogger(getClass());
				
				public void process(WatchedEvent event) {
					final boolean infoEnabled = this.logger.isInfoEnabled();
					if (infoEnabled) {
						this.logger.info("[Zookeeper]信息。typs：'{}'，state：'{}'，path：'{}'", event.getType(),
							event.getState(), event.getPath());
					}
				}
			});
		String group = this.offlineLocksProperties.getGroup() == null ? Apps.getAppName() : this.offlineLocksProperties
			.getGroup();
		String id = this.offlineLocksProperties.getId() == null ? IPUtil.getFirstNoLoopbackIPV4Address()
			: this.offlineLocksProperties.getId();
		if (StringUtils.isNotEmpty(rootDir)) {
			keeperLockManager = new ZooKeeperLockManager(zooKeeperFactory, rootDir, group, id, encode);
		} else {
			keeperLockManager = new ZooKeeperLockManager(zooKeeperFactory, group, id, encode);
		}
		TimeoutCondition timeoutCondition = this.offlineLocksProperties.getLockTimeout() <= 0 ? null
			: new TimeoutCondition(this.offlineLocksProperties.getLockTimeout());
		if (timeoutCondition != null) {
			keeperLockManager.setTimeoutCondition(timeoutCondition);
		}
		return keeperLockManager;
	}
	
	@Override
	public List<TCPEndpoint> endpoints() {
		List<TCPEndpoint> tcpEndpoints = Lists.newArrayList();
		if (isVm()) {
			return tcpEndpoints;
		}
		TCPEndpoint tcpEndpoint = new TCPEndpoint();
		tcpEndpoint.parseIpAndPort(this.commonProperties.getZkUrl());
		tcpEndpoint.setName("zk");
		tcpEndpoint.setDesc("offlinelocks");
		tcpEndpoints.add(tcpEndpoint);
		return tcpEndpoints;
	}
}
