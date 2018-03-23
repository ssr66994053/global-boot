/*
* www.yiji.com Inc.
* Copyright (c) 2014 All Rights Reserved
*/

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-27 13:58 创建
 *
 */

package com.yiji.boot.amqp;

import com.google.common.collect.Lists;
import com.yiji.framework.hera.client.exception.HeraException;
import com.yiji.framework.hera.client.listener.Event;
import com.yiji.framework.hera.client.listener.ValueTrigger;
import com.yjf.common.concurrent.MonitoredThreadPool;
import com.yjf.common.portrait.model.IOResource;
import com.yjf.common.portrait.model.TCPEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

/**
 * @author yanglie@yiji.com
 */
@Configuration
@ConditionalOnProperty(value = "yiji.rabbitmq.enable", matchIfMissing = true)
@EnableConfigurationProperties({ RabbitProperties.class, RabbitExtensionProperties.class,
								RabbitThreadPoolProperties.class })
@EnableRabbit
public class RabbitCustomAutoConfiguration implements ValueTrigger, IOResource<TCPEndpoint> {
	private static final Logger logger = LoggerFactory.getLogger(RabbitCustomAutoConfiguration.class);
	public static final String RABBIT_CONNECTION_FACTORY = "rabbitConnectionFactory";
	
	private MonitoredThreadPool rabbitThreadPool;
	private CachingConnectionFactory cachingConnectionFactory;
	@Autowired
	private RabbitProperties config;
	
	@Bean
	public MessageConverter kyroMessageConverter() {
		return new KyroMessageConverter();
	}
	
	@Bean
	public AmqpAdmin rabbitAdmin(@Qualifier(RABBIT_CONNECTION_FACTORY) CachingConnectionFactory rabbitConnectionFactory) {
		return new RabbitAdmin(rabbitConnectionFactory);
	}
	
	@Bean
	public CachingConnectionFactory rabbitConnectionFactory(RabbitProperties config,
															RabbitExtensionProperties extensionConfig) throws Exception {
		RabbitConnectionFactoryBean factory = new RabbitConnectionFactoryBean();
		if (config.getHost() != null) {
			factory.setHost(config.getHost());
			factory.setPort(config.getPort());
		}
		if (config.getUsername() != null) {
			factory.setUsername(config.getUsername());
		}
		if (config.getPassword() != null) {
			factory.setPassword(config.getPassword());
		}
		if (config.getVirtualHost() != null) {
			factory.setVirtualHost(config.getVirtualHost());
		}
		if (config.getRequestedHeartbeat() != null) {
			factory.setRequestedHeartbeat(config.getRequestedHeartbeat());
		} else {
			//默认要有request heartbeat, 设为20s
			factory.setRequestedHeartbeat(20);
		}
		factory.setConnectionTimeout(extensionConfig.getConnectionTimeout());
		//RabbitProperties.Ssl ssl = config.getSsl();
		//if (ssl.isEnabled()) {
		//	factory.setUseSSL(true);
		//	if (ssl.getKeyStore() != null || ssl.getTrustStore() != null) {
		//		Properties properties = ssl.createSslProperties();
		//		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		//		properties.store(outputStream, "SSL config");
		//		factory.setSslPropertiesLocation(new ByteArrayResource(outputStream.toByteArray()));
		//	}
		//}
		factory.afterPropertiesSet();
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(factory.getObject());
		connectionFactory.setAddresses(config.getAddresses());
		connectionFactory.setChannelCacheSize(extensionConfig.getCacheChannelSize());
		return connectionFactory;
	}
	
	@Bean
	public RabbitTemplate rabbitTemplate(	@Qualifier(RABBIT_CONNECTION_FACTORY) CachingConnectionFactory rabbitConnectionFactory,
											MessageConverter kyroMessageConverter) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(rabbitConnectionFactory);
		rabbitTemplate.setMessageConverter(kyroMessageConverter);
		return rabbitTemplate;
	}
	
	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(	@Qualifier(RABBIT_CONNECTION_FACTORY) CachingConnectionFactory rabbitConnectionFactory,
																				RabbitProperties config,
																				ThreadPoolTaskExecutor rabbitmqTaskExecutor,
																				MessageConverter kyroMessageConverter,
																				RabbitExtensionProperties extensionProperties) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(rabbitConnectionFactory);
		RabbitProperties.Listener listenerConfig = config.getListener();
		factory.setAutoStartup(listenerConfig.isAutoStartup());
		if (listenerConfig.getConcurrency() != null) {
			factory.setConcurrentConsumers(listenerConfig.getConcurrency());
		}
		if (listenerConfig.getAcknowledgeMode() != null) {
			factory.setAcknowledgeMode(listenerConfig.getAcknowledgeMode());
		}
		if (listenerConfig.getConcurrency() == null) {
			factory.setConcurrentConsumers(extensionProperties.getListenerConcurrency());
		} else {
			factory.setConcurrentConsumers(listenerConfig.getConcurrency());
		}
		if (listenerConfig.getMaxConcurrency() != null) {
			factory.setMaxConcurrentConsumers(listenerConfig.getMaxConcurrency());
		}
		if (listenerConfig.getPrefetch() != null) {
			factory.setPrefetchCount(listenerConfig.getPrefetch());
		}
		if (listenerConfig.getTransactionSize() != null) {
			factory.setTxSize(listenerConfig.getTransactionSize());
		}
		factory.setTaskExecutor(rabbitmqTaskExecutor);
		factory.setMessageConverter(kyroMessageConverter);
		return factory;
	}
	
	@Bean
	public SimpleRabbitListenerContainerFactory compatibleRabbitListenerContainerFactory(	@Qualifier(RABBIT_CONNECTION_FACTORY) CachingConnectionFactory rabbitConnectionFactory,
																							RabbitProperties config,
																							ThreadPoolTaskExecutor rabbitmqTaskExecutor,
																							RabbitExtensionProperties extensionProperties) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(rabbitConnectionFactory);
		RabbitProperties.Listener listenerConfig = config.getListener();
		factory.setAutoStartup(listenerConfig.isAutoStartup());
		if (listenerConfig.getAcknowledgeMode() != null) {
			factory.setAcknowledgeMode(listenerConfig.getAcknowledgeMode());
		}
		if (listenerConfig.getConcurrency() == null) {
			factory.setConcurrentConsumers(extensionProperties.getListenerConcurrency());
		} else {
			factory.setConcurrentConsumers(listenerConfig.getConcurrency());
		}
		
		if (listenerConfig.getMaxConcurrency() != null) {
			factory.setMaxConcurrentConsumers(listenerConfig.getMaxConcurrency());
		}
		if (listenerConfig.getPrefetch() != null) {
			factory.setPrefetchCount(listenerConfig.getPrefetch());
		}
		if (listenerConfig.getTransactionSize() != null) {
			factory.setTxSize(listenerConfig.getTransactionSize());
		}
		factory.setTaskExecutor(rabbitmqTaskExecutor);
		return factory;
	}
	
	@Bean
	public ThreadPoolTaskExecutor rabbitmqTaskExecutor(RabbitThreadPoolProperties rabbitThreadPoolProperties,
														RabbitProperties config) {
		MonitoredThreadPool taskExecutor = new MonitoredThreadPool();
		taskExecutor.setCorePoolSize(rabbitThreadPoolProperties.getCorePoolSize());
		taskExecutor.setKeepAliveSeconds(300);
		taskExecutor.setMaxPoolSize(rabbitThreadPoolProperties.getMaxPoolSize());
		taskExecutor.setQueueCapacity(rabbitThreadPoolProperties.getQueueCapacity());
		taskExecutor.setAwaitTerminationSeconds(60);
		taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
		taskExecutor.setDaemon(false);
		// 线程池名字不要改, 因为opentsdb里面已经使用线程池名字作为metric名字的一部分, 改了会报错
		// 另外, 线程池名字里面最好不要含有变量, 比如ip, 端口号之类, 有变量不方便收集metrics
		taskExecutor.setThreadNamePrefix("RabbitContainerThread-");
		rabbitThreadPool = taskExecutor;
		return taskExecutor;
	}
	
	@Override
	public void onChange(Event event) throws HeraException {
		event.ifPresent("yiji.rabbitmq.pool.maxPoolSize", value -> {
			if (rabbitThreadPool != null) {
				rabbitThreadPool.setMaxPoolSize(Integer.valueOf(value));
				logger.info("hera设置rabbit客户端maxPoolSize={}", value);
			}
			
		});
		event.ifPresent("yiji.rabbitmq.cacheChannelSize", value -> {
			if (cachingConnectionFactory != null) {
				cachingConnectionFactory.setChannelCacheSize(Integer.valueOf(value));
				logger.info("hera设置rabbit客户端cacheChannelSize={}", value);
			}
			
		});
	}
	
	@Override
	public List<TCPEndpoint> endpoints() {
		TCPEndpoint tcpEndpoint = new TCPEndpoint();
		tcpEndpoint.setName("amqp");
		tcpEndpoint.setPort(config.getPort());
		tcpEndpoint.setIp(config.getHost());
		return Lists.newArrayList(tcpEndpoint);
	}
	
	@Bean
	public RabbitHealthIndicator rabbitHealthIndicator(@Qualifier("rabbitTemplate") RabbitTemplate rabbitTemplate) {
		return new RabbitHealthIndicator(rabbitTemplate);
	}
}
