/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-27 15:32 创建
 *
 */
package com.global.boot.test.amqp.wqueue;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yanglie@yiji.com
 */

@Configuration
public class WQProducer {
	final static String queueName = "spring-boot-work-queue-test";
	@Autowired(required = false)
	RabbitTemplate rabbitTemplate;
	
	/**
	 * 创建一个名为queueName的队列
	 * @return
	 */
	@Bean
	Queue queue() {
		return new Queue(queueName);
	}
	
	/**
	 * 发送消息
	 * @param object
	 */
	public void send(Object object) {
		rabbitTemplate.setQueue(queueName);
		rabbitTemplate.convertAndSend(queueName, object);
	}
}
