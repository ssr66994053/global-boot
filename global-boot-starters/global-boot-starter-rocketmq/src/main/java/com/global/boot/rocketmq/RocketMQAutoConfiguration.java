/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-01-29 13:41 创建
 * qiubo@yiji.com 2016-02-22 18:06 增加messageProducer启动和关闭方法/rocketListenerAnnotationBeanPostProcessor改为静态方法
 *
 *
 */
package com.global.boot.rocketmq;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.google.common.collect.Lists;
import com.global.boot.rocketmq.consumer.RocketListenerAnnotationBeanPostProcessor;
import com.global.boot.rocketmq.consumer.RocketMQConsumer;
import com.global.boot.rocketmq.producer.MessageProducer;
import com.global.boot.rocketmq.producer.MessageProducerLifeManager;
import com.global.boot.rocketmq.support.KyroMessageConverter;
import com.yjf.common.portrait.model.IOResource;
import com.yjf.common.portrait.model.TCPEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author yanglie@yiji.com
 * @author qiubo@yiji.com
 */
@SuppressWarnings("ALL")
@Configuration
@ConditionalOnProperty(value = "yiji.rocketmq.enable", matchIfMissing = true)
@EnableConfigurationProperties({ RocketMQProperties.class })
public class RocketMQAutoConfiguration implements IOResource<TCPEndpoint> {
	@Autowired
	private RocketMQProperties rocketMQProperties;
	
	@Bean
	@ConditionalOnProperty(value = "yiji.rocketmq.producer.enable", matchIfMissing = false)
	public DefaultMQProducer defaultMQProducer(RocketMQProperties rocketMQProperties) throws MQClientException {
		DefaultMQProducer defaultMQProducer = new DefaultMQProducer(rocketMQProperties.getProducer().getGroup());
		defaultMQProducer.setNamesrvAddr(rocketMQProperties.getNameSrvAddr());
		defaultMQProducer.setRetryAnotherBrokerWhenNotStoreOK(true);
		return defaultMQProducer;
	}
	
	@Bean(destroyMethod = "stop", initMethod = "start")
	@ConditionalOnProperty(value = "yiji.rocketmq.producer.enable", matchIfMissing = false)
	public MessageProducer messageProducer(DefaultMQProducer defaultMQProducer, RocketMQProperties rocketMQProperties) {
		MessageProducer messageProducer = new MessageProducer(defaultMQProducer, new KyroMessageConverter(),
			rocketMQProperties.getProducer().isLogEnable());
//		messageProducer.setDisableTopics(rocketMQProperties.getDisableTopics());
		return messageProducer;
	}
	
	@Bean
	@ConditionalOnProperty(value = "yiji.rocketmq.producer.enable", matchIfMissing = false)
	public MessageProducerLifeManager messageProducerLifeManager() {
		return new MessageProducerLifeManager();
	}
	
	@Bean
	@ConditionalOnProperty(value = "yiji.rocketmq.consumer.enable", matchIfMissing = false)
	public RocketMQConsumer rocketMQConsumer(RocketMQProperties rocketMQProperties) throws MQClientException {
		RocketMQConsumer rocketMQConsumer = new RocketMQConsumer(rocketMQProperties.getConsumer().getGroup(),
			rocketMQProperties.getNameSrvAddr(), rocketMQProperties.getConsumer().getConsumeThreadMin(),
			rocketMQProperties.getConsumer().getConsumeThreadMax());
		return rocketMQConsumer;
	}
	
	@Bean
	@ConditionalOnProperty(value = "yiji.rocketmq.consumer.enable", matchIfMissing = false)
	public static RocketListenerAnnotationBeanPostProcessor rocketListenerAnnotationBeanPostProcessor() {
		return new RocketListenerAnnotationBeanPostProcessor();
	}
	
	@Bean
	@ConditionalOnProperty(value = "yiji.rocketmq.consumer.enable", matchIfMissing = false)
	public RocketMQConsumer.RocketMQConsumerContextRefreshedEventListener rocketMQConsumerContextRefreshedEventListener(RocketMQConsumer rocketMQConsumer)
																																							throws MQClientException {
		RocketMQConsumer.RocketMQConsumerContextRefreshedEventListener rocketMQConsumerContextRefreshedEventListener = new RocketMQConsumer.RocketMQConsumerContextRefreshedEventListener(
			rocketMQConsumer);
		return rocketMQConsumerContextRefreshedEventListener;
	}
	
	@Bean
	@ConditionalOnProperty(value = "yiji.rocketmq.consumer.enable", matchIfMissing = false)
	public RocketMQConsumer.RocketMQConsumerContextClosedEventListener rocketMQConsumerContextClosedEventListener(	RocketMQConsumer rocketMQConsumer)
																																						throws MQClientException {
		RocketMQConsumer.RocketMQConsumerContextClosedEventListener rocketMQConsumerContextClosedEventListener = new RocketMQConsumer.RocketMQConsumerContextClosedEventListener(
			rocketMQConsumer);
		return rocketMQConsumerContextClosedEventListener;
	}
	
	@Override
	public List<TCPEndpoint> endpoints() {
		TCPEndpoint tcpEndpoint = new TCPEndpoint();
		String address = rocketMQProperties.getNameSrvAddr();
		tcpEndpoint.setName("rocketmq");
		tcpEndpoint.parseIpAndPort(address);
		return Lists.newArrayList(tcpEndpoint);
	}
}
