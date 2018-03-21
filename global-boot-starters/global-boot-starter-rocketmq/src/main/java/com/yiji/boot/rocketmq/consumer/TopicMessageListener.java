/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-02-01 15:05 创建
 * qiubo@yiji.com  2016-02-23 15:05 重构,加MDC
 *
 */
package com.yiji.boot.rocketmq.consumer;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.yiji.boot.core.configuration.LogAutoConfiguration;
import com.yiji.boot.rocketmq.message.NotifyMessage;
import com.yiji.boot.rocketmq.support.MessageConverter;
import com.yjf.common.util.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.messaging.handler.HandlerMethod;

import java.lang.reflect.InvocationTargetException;

/**
 * @author yanglie@yiji.com
 * @author qiubo@yiji.com
 */
public class TopicMessageListener {
	private static final Logger logger = LoggerFactory.getLogger(TopicMessageListener.class);
	
	private boolean enableLog;
	private String topic;
	private String tags;
	
	private HandlerMethod handlerMethod;
	private MessageConverter messageConverter;
	
	public void onMessage(MessageExt message, ConsumeConcurrentlyContext context) throws InvocationTargetException,
																					IllegalAccessException {
		handle(messageConverter.fromMessage(message), context.getMessageQueue().getTopic());
	}
	
	private void handle(NotifyMessage message, String topic) throws InvocationTargetException, IllegalAccessException {
		try {
			MDC.put(LogAutoConfiguration.LogProperties.GID_KEY, message.getGid());
			if (enableLog) {
				logger.info("队列:{},收到消息:{}", topic, ToString.toString(message));
			}
			if (tags.contains(message.getTag())) {
				Object[] args = { message };
				handlerMethod.getMethod().invoke(handlerMethod.getBean(), args);
			}
			if (enableLog) {
				logger.info("消息完成");
			}
		} finally {
			MDC.clear();
		}
	}
	
	public void onMessage(MessageExt message, ConsumeOrderlyContext context) throws InvocationTargetException,
																			IllegalAccessException {
		handle(messageConverter.fromMessage(message), context.getMessageQueue().getTopic());
	}
	
	public String getTopic() {
		return topic;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	public String getTags() {
		return tags;
	}
	
	public void setTags(String tags) {
		this.tags = tags;
	}
	
	public void setHandlerMethod(HandlerMethod handlerMethod) {
		this.handlerMethod = handlerMethod;
	}
	
	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}
	
	public String toString() {
		return "TopicMessageListener: " + "(" + topic + ")" + "(" + tags + ")" + "(" + handlerMethod.toString() + ")";
	}
	
	public boolean isEnableLog() {
		return enableLog;
	}
	
	public void setEnableLog(boolean enableLog) {
		this.enableLog = enableLog;
	}
}
