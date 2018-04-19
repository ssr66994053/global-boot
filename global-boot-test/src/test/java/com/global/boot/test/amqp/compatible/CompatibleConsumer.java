/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-09-15 11:35 创建
 *
 */
package com.global.boot.test.amqp.compatible;

import com.global.common.kryo.Kryos;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author yanglie@yiji.com
 */
@Component
public class CompatibleConsumer {
	private final static String queueName = "spring-boot-compatible";
	private volatile boolean ready = false;
	private MyMessage lastReceiveMessage;
	
	/**
	 * 定义一个消息的监听处理方法，会接收到queueName的上的消息，并进行处理
	 * @param message
	 */
	@RabbitListener(queues = queueName, containerFactory = "compatibleRabbitListenerContainerFactory")
	public void process(Message message) {
		ready = true;
		lastReceiveMessage = Kryos.readObject(message.getBody(), MyMessage.class);
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	
	public MyMessage getLastReceiveMessage() {
		return lastReceiveMessage;
	}
	
	public void setLastReceiveMessage(MyMessage lastReceiveMessage) {
		this.lastReceiveMessage = lastReceiveMessage;
	}
}
