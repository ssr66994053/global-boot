/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-06 15:41 创建
 *
 */
package com.global.boot.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.config.*;
import com.alibaba.dubbo.config.spring.AnnotationBean;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.global.boot.core.AppConfigException;
import com.global.boot.core.Apps;
import com.global.boot.core.CommonProperties;
import com.global.boot.core.EnvironmentHolder;
import com.global.boot.core.listener.AppInfoWriter;
//import com.global.framework.hera.client.exception.HeraException;
//import com.global.framework.hera.client.listener.Event;
//import com.global.framework.hera.client.listener.ValueTrigger;
import com.global.common.dubbo.DubboRemoteProxyFacotry;
import com.global.common.dubbo.DubboShutdownApplicationListener;
import com.global.common.portrait.model.IOResource;
import com.global.common.portrait.model.TCPEndpoint;
import com.global.common.util.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;

import java.util.List;
import java.util.Map;

/**
 * @author qiubo@yiji.com
 */
@Configuration
@EnableConfigurationProperties({ DubboProperties.class, CommonProperties.class })
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(value = "yiji.dubbo.enable", matchIfMissing = true)
public class DubboAutoConfiguration implements InitializingBean, IOResource<TCPEndpoint> {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DubboAutoConfiguration.class);
	public static final String DEFAULT_SCAN_PACAKGE = "com.global.boot," + Apps.getBasePackage();

	@Autowired
	private ApplicationContext applicationContext;
	
	private static DubboProperties dubboProperties;
	
	@Override
	public void afterPropertiesSet() throws Exception {
	}
	
	private static void initDubboProperties() {
		try {
			if (dubboProperties == null) {
				DubboProperties tmp = Apps.buildProperties(DubboProperties.class);
				logger.info("启用dubbo组件:{}",tmp);
				DubboAutoConfiguration.dubboProperties = tmp;
			}
		} catch (Exception e) {
			throw new AppConfigException("dubbo配置错误", e);
		}
	}
	
	@Bean
	public static ApplicationConfig applicationConfig() {
		initDubboProperties();
		ApplicationConfig config = new ApplicationConfig();
		config.setName(Apps.getAppName());
		config.setOwner(dubboProperties.getOwner());
		Apps.exposeInfo("dubbo.owner", config.getOwner());
		if (!Strings.isNullOrEmpty(dubboProperties.getVersion())) {
			config.setVersion(dubboProperties.getVersion());
		}
		return config;
	}
	
	@Bean
	@DependsOn("applicationConfig")
	public static RegistryConfig registryConfig() {
		initDubboProperties();
		RegistryConfig config = new RegistryConfig();
		config.setProtocol("zookeeper");
		EnvironmentHolder.RelaxedProperty relaxedProperty = new EnvironmentHolder.RelaxedProperty(
			CommonProperties.PREFIX, "zkUrl");
		String zkUrl = relaxedProperty.getProperty();
		if (Strings.isNullOrEmpty(zkUrl)) {
			throw new AppConfigException("dubbo组件请配置zookeeper地址,key=" + relaxedProperty.getPropertyName());
		}
		
		logger.info("dubbo使用注册中心地址:{}, 是否注册:{}", zkUrl, dubboProperties.isRegister());
		config.setRegister(dubboProperties.isRegister());
		config.setAddress(zkUrl);
		config.setFile(Apps.getAppDataPath() + "/dubbo/dubbo.cache");
		return config;
	}
	
	@Bean
	@ConditionalOnProperty(value = "yiji.dubbo.refOnlyZkUrl1")
	@DependsOn("applicationConfig")
	public static RegistryConfig refOnlyRegistryConfig1() {
		initDubboProperties();
		final String refOnlyZkUrl1 = dubboProperties.getRefOnlyZkUrl1();
		if (StringUtils.isBlank(refOnlyZkUrl1)) {
			throw new AppConfigException("yiji.dubbo.refOnlyZkUrl1 属性的值不能为空!");
		}
		return createRefOnlyRegistry(refOnlyZkUrl1, "1");
	}
	
	@Bean
	@ConditionalOnProperty(value = "yiji.dubbo.refOnlyZkUrl2")
	@DependsOn("applicationConfig")
	public static RegistryConfig refOnlyRegistryConfig2() {
		initDubboProperties();
		final String refOnlyZkUrl2 = dubboProperties.getRefOnlyZkUrl2();
		if (StringUtils.isBlank(refOnlyZkUrl2)) {
			throw new AppConfigException("yiji.dubbo.refOnlyZkUrl2 属性的值不能为空!");
		}
		return createRefOnlyRegistry(refOnlyZkUrl2, "2");
	}
	
	@Bean
	@ConditionalOnProperty(value = "yiji.dubbo.refOnlyZkUrl3")
	@DependsOn("applicationConfig")
	public static RegistryConfig refOnlyRegistryConfig3() {
		initDubboProperties();
		final String refOnlyZkUrl3 = dubboProperties.getRefOnlyZkUrl3();
		if (StringUtils.isBlank(refOnlyZkUrl3)) {
			throw new AppConfigException("yiji.dubbo.refOnlyZkUrl3 属性的值不能为空!");
		}
		return createRefOnlyRegistry(refOnlyZkUrl3, "3");
	}
	
	private static RegistryConfig createRefOnlyRegistry(String zkUrl, String id) {
		RegistryConfig config = new RegistryConfig();
		config.setProtocol("zookeeper");
		String cacheFile = "/dubbo/dubbo.cache.refOnly" + id;
		
		logger.info("dubbo使用注册中心(只消费)地址:{}", zkUrl);
		config.setAddress(zkUrl);
		config.setFile(Apps.getAppDataPath() + cacheFile);
		config.setRegister(false);
		return config;
	}
	
	@Bean
	@ConditionalOnProperty(value = "yiji.dubbo.provider.enable", matchIfMissing = true)
	@DependsOn("applicationConfig")
	public static ProtocolConfig protocolConfig() {
		initDubboProperties();
		ProtocolConfig config = new ProtocolConfig();
		config.setName("dubbo");
		config.setPort(dubboProperties.getProvider().getPort());
		new AppInfoWriter().write("app.dubboport", dubboProperties.getProvider().getPort());
		Apps.exposeInfo("dubbo.port", dubboProperties.getProvider().getPort());
		//配置线程池
		config.setThreadpool("yijiDubbo");
		config.setThreads(dubboProperties.getProvider().getMaxThreads());
		int queueSize = dubboProperties.getProvider().getQueue();
		config.setQueues(queueSize);
		Map<String, String> params = Maps.newHashMap();
		params.put(Constants.CORE_THREADS_KEY, dubboProperties.getProvider().getCorethreads().toString());
		params.put(Constants.ALIVE_KEY, dubboProperties.getProvider().getKeepAliveTime().toString());
		config.setParameters(params);
		//设置序列化协议,如果不设置,使用dubbo默认协议 hessian2
		if (!Strings.isNullOrEmpty(dubboProperties.getProvider().getSerialization())) {
			if ("hessian3".equals(dubboProperties.getProvider().getSerialization())) {
				logger.info("dubbo启用数据压缩特性");
			}
			config.setSerialization(dubboProperties.getProvider().getSerialization());
		}
		return config;
	}
	
	@Bean
	@ConditionalOnProperty(value = "yiji.dubbo.provider.enable", matchIfMissing = true)
	public static ProviderConfig providerConfig() {
		initDubboProperties();
		ProviderConfig config = new ProviderConfig();
		config.setTimeout(dubboProperties.getProvider().getTimeout());
		config.setCluster("failfast");
		config.setRegister(dubboProperties.getProvider().isRegister());
		if (dubboProperties.isProviderLog()) {
			config.setFilter("providerLogFilter");
		}
		return config;
	}
	
	@Bean
	@DependsOn({ "applicationConfig", "registryConfig" })
	public static ConsumerConfig consumerConfig() {
		initDubboProperties();
		ConsumerConfig config = new ConsumerConfig();
		config.setCheck(false);
		config.setLoadbalance("roundrobin");
		if (dubboProperties.isConsumerLog()) {
			config.setFilter("requestLogFilter");
		}
		return config;
	}
	
	@Bean
	@DependsOn({ "registryConfig" })
	public static MonitorConfig monitorConfig() {
		MonitorConfig config = new MonitorConfig();
		config.setProtocol("registry");
		return config;
	}
	
	@Bean
	public DubboRemoteProxyFacotry dubboRemoteProxyFacotry() {
		return new DubboRemoteProxyFacotry();
	}
	
	@Bean
	public DubboShutdownApplicationListener dubboShutdownApplicationListener() {
		return new DubboShutdownApplicationListener();
	}
	
	@Bean
	public static AnnotationBean annotationBean() {
		initDubboProperties();
		AnnotationBean config = new AnnotationBean();
		String scanPackage=dubboProperties.getScanPackage();
		if(Strings.isNullOrEmpty(scanPackage)){
			scanPackage= DEFAULT_SCAN_PACAKGE;
		}else {
			scanPackage+=","+DEFAULT_SCAN_PACAKGE;
		}
		//扫描路径包括boot插件包和应用所在的包
		config.setPackage(scanPackage);
		return config;
	}
	
	@Bean
	public DubboHealthIndicator dubboHealthIndicator() {
		return new DubboHealthIndicator();
	}
	
//	@Override
//	public void onChange(Event event) throws HeraException {
//		event.ifPresent("yiji.dubbo.provider.maxThreads", maxThreads -> {
//			if (YijiDubboThreadPool.threadPoolExecutor != null) {
//				YijiDubboThreadPool.threadPoolExecutor.setMaximumPoolSize(Integer.valueOf(maxThreads));
//				logger.info("hera设置dubbo maxThreads={}", maxThreads);
//			}
//			
//		});
//	}
	
	@Override
	public List<TCPEndpoint> endpoints() {
		List<TCPEndpoint> tcpEndpoints = Lists.newArrayList();
		Map<String, RegistryConfig> beansOfType = applicationContext.getBeansOfType(RegistryConfig.class);
		for (RegistryConfig registryConfig : beansOfType.values()) {
			TCPEndpoint tcpEndpoint = new TCPEndpoint();
			tcpEndpoint.parseIpAndPort(registryConfig.getAddress());
			tcpEndpoint.setName("zk");
			tcpEndpoint.setDesc("dubbo");
			tcpEndpoints.add(tcpEndpoint);
		}
		return tcpEndpoints;
	}
	
	@Bean
	public DubboEndpoint dubboEndpoint() {
		return new DubboEndpoint();
	}
}
