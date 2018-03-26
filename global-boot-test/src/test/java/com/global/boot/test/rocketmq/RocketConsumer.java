/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-02-15 09:43 创建
 *
 */
package com.global.boot.test.rocketmq;

import com.google.common.collect.Sets;
import com.global.boot.rocketmq.consumer.MessageFilter;
import com.global.boot.rocketmq.consumer.RocketListener;
import com.global.boot.rocketmq.message.NotifyMessage;
import com.global.boot.rocketmq.message.OrderedNotifyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author yanglie@yiji.com
 */
@Component
public class RocketConsumer {
	
	private static final Logger logger = LoggerFactory.getLogger(RocketConsumer.class);
	
	private Set<String> ids = Sets.newConcurrentHashSet();
	
	/**
	 * 定义一个消息的监听处理方法，会接收到topic上的消息，并进行处理
	 * @param message
	 */
	@RocketListener(topic = "TopicTest", ordered = true, messageFilter = @MessageFilter(fromSystem = "yiji-boot-test",
			event = { "event_trade", "event_deposit" }))
	public void processMessage(OrderedNotifyMessage message) {
		ids.add(message.getId());
	}
	
	/**
	 * 接收accountant发送的广播消息
	 * @param message
	 */
	@RocketListener(topic = "TopicTest", messageFilter = @MessageFilter(fromSystem = { "yiji-boot-test" },
			toSystem = { "cashier" }, event = { "event_trade", "event-deposit" }))
	public void processBroadcastMessage(NotifyMessage message) {
		ids.add(message.getId());
	}
	
	public Set<String> getIds() {
		return ids;
	}
}
