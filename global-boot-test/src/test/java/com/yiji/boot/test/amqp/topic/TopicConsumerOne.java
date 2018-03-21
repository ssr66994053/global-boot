/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-28 10:41 创建
 *
 */
package com.yiji.boot.test.amqp.topic;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author yanglie@yiji.com
 */
@Component
public class TopicConsumerOne {
	private final static String queueName = "spring-boot-topic-one";
	private volatile boolean ready = false;
	private MyMessage lastReceiveMessage;
	
	/**
	 * 定义一个消息的监听处理方法，会接收到queueName的上的消息，并进行处理
	 * @param message
	 */
	@RabbitListener(queues = queueName)
	public void processMessage(MyMessage message) {
		//处理收到的rabbitmq信息
		lastReceiveMessage = message;
		ready = true;
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public MyMessage getLastReceiveMessage() {
		return lastReceiveMessage;
	}
}
