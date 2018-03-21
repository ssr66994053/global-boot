/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-01-29 10:35 创建
 * qiubo@yiji.com 2016-02-22 18:06 优化性能/参数检查/重构
 *
 */
package com.yiji.boot.rocketmq.producer;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.yiji.boot.rocketmq.exception.TopicDisableException;
import com.yiji.boot.rocketmq.message.NotifyMessage;
import com.yiji.boot.rocketmq.message.OrderedNotifyMessage;
import com.yiji.boot.rocketmq.support.MessageConverter;
import com.yiji.framework.hera.client.support.annotation.AutoConfig;
import com.yjf.common.util.StringUtils;
import com.yjf.common.util.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author yanglie@yiji.com
 * @author qiubo@yiji.com
 */
public class MessageProducer {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageProducer.class);
	private MQProducer defaultMQProducer;
	private MessageConverter messageConverter;
	private MessageQueueSelector messageQueueSelector = (mqs, msg, arg) -> {
		Integer id = Math.abs(arg.hashCode());
		int index = id % mqs.size();
		MessageQueue messageQueue = mqs.get(index);
		return messageQueue;
	};
	/**
	 * 通过配置管理系统的热切换功能来实现动态禁止往指定的topic发送消息
	 */
	@AutoConfig("yiji.rocketmq.disableTopics")
	private volatile String disableTopics;
	
	private boolean logEnable;
	/**
	 * 使用CopyOnWriteArraySet来保证线程安全
	 */
	private Set<String> disableTopicsSet = new CopyOnWriteArraySet<>();
	
	public MessageProducer(MQProducer defaultMQProducer, MessageConverter messageConverter, boolean logEnable) {
		this.defaultMQProducer = defaultMQProducer;
		this.messageConverter = messageConverter;
		this.logEnable = logEnable;
	}
	
	public MessageResult send(NotifyMessage notifyMessage) throws InterruptedException, RemotingException,
															MQClientException, MQBrokerException, TopicDisableException {
		Assert.notNull(notifyMessage);
		notifyMessage.check();
		if (disableTopicsSet.contains(notifyMessage.getTopic())) {
			throw new TopicDisableException("Topic:" + notifyMessage.getTopic() + "暂时被禁用了");
		}
		Message message = messageConverter.toMessage(notifyMessage);
		SendResult result;
		if (logEnable) {
			logger.info("发送消息:{}, 大小:{}", ToString.toString(notifyMessage), message.getBody().length);
		}
		if (notifyMessage instanceof OrderedNotifyMessage) {
			result = defaultMQProducer.send(message, messageQueueSelector,
				((OrderedNotifyMessage) notifyMessage).getGroupId());
		} else {
			result = defaultMQProducer.send(message);
		}
		if (logEnable) {
			logger.info("消息发送结果：mgsId={},status={}", result.getMsgId(), result.getSendStatus());
		}
		return new MessageResult(result);
	}
	
	public void start() throws MQClientException {
		logger.info("启动RocketMQ Producer");
		defaultMQProducer.start();
	}
	
	public void stop() {
		logger.info("关闭RocketMQ Producer");
		defaultMQProducer.shutdown();
	}
	
	public String getDisableTopics() {
		return disableTopics;
	}
	
	public void setDisableTopics(String disableTopics) {
		this.disableTopics = disableTopics;
		disableTopicsSet.clear();
		if (StringUtils.isNotBlank(disableTopics)) {
			for (String topic : this.disableTopics.split(",")) {
				disableTopicsSet.add(topic.trim());
			}
		}
	}
	
}
