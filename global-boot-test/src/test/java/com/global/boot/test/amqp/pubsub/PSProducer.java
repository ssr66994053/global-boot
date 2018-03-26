/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-28 10:19 创建
 *
 */
package com.global.boot.test.amqp.pubsub;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yanglie@yiji.com
 */
@Configuration
public class PSProducer {
	final static String queueNameOne = "spring-boot-pub-sub-one";
	final static String queueNametwo = "spring-boot-pub-sub-two";
	final static String exchangeName = "spring-boot-pub-exchange";
	@Autowired(required = false)
	RabbitTemplate rabbitTemplate;
	
	@Bean
	Queue queueOne() {
		return new Queue(queueNameOne);
	}
	
	@Bean
	Queue queueTwo() {
		return new Queue(queueNametwo);
	}
	
	@Bean
	FanoutExchange fanoutExchange() {
		return new FanoutExchange(exchangeName);
	}
	
	@Bean
	Binding bindingOne(Queue queueOne, FanoutExchange fanoutExchange) {
		return BindingBuilder.bind(queueOne).to(fanoutExchange);
	}
	
	@Bean
	Binding bindingTwo(Queue queueTwo, FanoutExchange fanoutExchange) {
		return BindingBuilder.bind(queueTwo).to(fanoutExchange);
	}
	
	/**
	 * 发送消息
	 * @param object
	 */
	public void send(Object object) {
		rabbitTemplate.convertAndSend(exchangeName, exchangeName, object);
	}
}
