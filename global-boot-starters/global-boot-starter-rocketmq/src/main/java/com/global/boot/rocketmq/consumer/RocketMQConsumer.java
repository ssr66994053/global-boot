/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-02-01 14:54 创建
 *
 */
package com.global.boot.rocketmq.consumer;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.*;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.global.common.lang.exception.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yanglie@yiji.com
 * @author qiubo@yiji.com
 */
public class RocketMQConsumer {
	private static final Logger logger = LoggerFactory.getLogger(RocketMQConsumer.class);
	private DefaultMQPushConsumer defaultMQPushConsumer;
	private DefaultMQPushConsumer orderedMQPushConsumer;
	private String groupName;
	private String nameAddr;
	private int consumeThreadMin = 5;
	private int consumeThreadMax = 10;
	private Map<String, TopicMessageListener> topicMessageListenerMap = new ConcurrentHashMap();
	private Map<String, TopicMessageListener> orderedTopicMessageListenerMap = new ConcurrentHashMap();
	
	public RocketMQConsumer(String groupName, String nameAddr, int consumeThreadMin, int consumeThreadMax)
																											throws MQClientException {
		this.groupName = groupName;
		this.nameAddr = nameAddr;
		this.consumeThreadMin = consumeThreadMin;
		this.consumeThreadMax = consumeThreadMax;
		//普通消费端指定组名加common后缀
		defaultMQPushConsumer = new DefaultMQPushConsumer(this.groupName + "_common");
		defaultMQPushConsumer.setNamesrvAddr(this.nameAddr);
		defaultMQPushConsumer.setConsumeThreadMin(this.consumeThreadMin);
		defaultMQPushConsumer.setConsumeThreadMax(this.consumeThreadMax);
		//设置一个新的订阅组第一次启动从队列的最前位置开始消费, 后续再启动接着上次消费的进度开始消费
		defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
		//顺序消费端指定组名加ordered后缀
		orderedMQPushConsumer = new DefaultMQPushConsumer(groupName + "_ordered");
		orderedMQPushConsumer.setNamesrvAddr(nameAddr);
		orderedMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
		logger.info("构建RocketMQConsumer,groupName={}, nameAddr={},consumeThreadMin={},consumeThreadMax={}", groupName,
			nameAddr, consumeThreadMin, consumeThreadMax);
	}
	
	public void addTopicListener(TopicMessageListener topicMessageListener) {
		if (topicMessageListenerMap.get(topicMessageListener.getTopic()) != null) {
			throw Exceptions.newRuntimeException("同一个topic不请允许存在多个普通消息的RocketListener，请检查配置");
		}
		topicMessageListenerMap.put(topicMessageListener.getTopic(), topicMessageListener);
	}
	
	public void addOrderTopicListener(TopicMessageListener topicMessageListener) {
		if (orderedTopicMessageListenerMap.get(topicMessageListener.getTopic()) != null) {
			throw Exceptions.newRuntimeException("同一个topic不请允许存在多个顺序消息的RocketListener，请检查配置");
		}
		orderedTopicMessageListenerMap.put(topicMessageListener.getTopic(), topicMessageListener);
	}
	
	public void start() {
		try {
			startDefaultMQPushConsumer();
			startOrderedMQPushConsumer();
		} catch (MQClientException e) {
			throw Exceptions.newRuntimeException("RocketMQ消费端启动失败", e);
		}
	}
	
	protected void startDefaultMQPushConsumer() throws MQClientException {
		if (defaultMQPushConsumer != null) {
			if (topicMessageListenerMap.size() != 0) {
				registerSubscribes(defaultMQPushConsumer, topicMessageListenerMap);
				defaultMQPushConsumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
					try {
						invokerListener(context, msgs, topicMessageListenerMap);
					} catch (Exception e) {
						logger.error("调用消息监听方法出错", e);
						return ConsumeConcurrentlyStatus.RECONSUME_LATER;
					}
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				});
				defaultMQPushConsumer.start();
			}
		}
	}
	
	protected void startOrderedMQPushConsumer() throws MQClientException {
		if (orderedMQPushConsumer != null) {
			if (orderedTopicMessageListenerMap.size() != 0) {
				registerSubscribes(orderedMQPushConsumer, orderedTopicMessageListenerMap);
				orderedMQPushConsumer.registerMessageListener((MessageListenerOrderly) (msgs, context) -> {
					try {
						invokerListener(context, msgs, orderedTopicMessageListenerMap);
					} catch (Exception e) {
						logger.error("调用消息监听方法出错", e);
						return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
					}
					return ConsumeOrderlyStatus.SUCCESS;
				});
				orderedMQPushConsumer.start();
			}
		}
	}
	
	protected void registerSubscribes(DefaultMQPushConsumer mqPushConsumer,
										Map<String, TopicMessageListener> listenerMap) throws MQClientException {
		for (Map.Entry<String, TopicMessageListener> entry : listenerMap.entrySet()) {
			mqPushConsumer.subscribe(entry.getKey(), entry.getValue().getTags());
		}
	}
	
	protected <T> void invokerListener(T context, List<MessageExt> msgs, Map<String, TopicMessageListener> listeners)
																														throws Exception {
		for (MessageExt messageExt : msgs) {
			TopicMessageListener listener = listeners.get(messageExt.getTopic());
			if (listener != null) {
				if (context instanceof ConsumeConcurrentlyContext) {
					listener.onMessage(messageExt, (ConsumeConcurrentlyContext) context);
				} else {
					listener.onMessage(messageExt, (ConsumeOrderlyContext) context);
				}
			} else {
				throw new IllegalArgumentException("没有找到主题" + messageExt.getTopic() + "对应的TopicListener");
			}
		}
	}
	
	public void stop() {
		if (defaultMQPushConsumer != null) {
			defaultMQPushConsumer.shutdown();
		}
		if (orderedMQPushConsumer != null) {
			orderedMQPushConsumer.shutdown();
		}
	}
	
	public static class RocketMQConsumerContextRefreshedEventListener implements
																		ApplicationListener<ContextRefreshedEvent> {
		
		private RocketMQConsumer consumer;
		
		public RocketMQConsumerContextRefreshedEventListener(RocketMQConsumer consumer) {
			this.consumer = consumer;
		}
		
		@Override
		public void onApplicationEvent(ContextRefreshedEvent event) {
			logger.info("容器初始化完毕，开始启动RocketMQConsumer");
			this.consumer.start();
		}
	}
	
	public static class RocketMQConsumerContextClosedEventListener implements ApplicationListener<ContextClosedEvent> {
		
		private RocketMQConsumer consumer;
		
		public RocketMQConsumerContextClosedEventListener(RocketMQConsumer consumer) {
			this.consumer = consumer;
		}
		
		@Override
		public void onApplicationEvent(ContextClosedEvent event) {
			logger.info("容器将要关闭，开始关闭RocketMQConsumer");
			this.consumer.stop();
		}
	}
	
}
