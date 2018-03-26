/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-28 10:19 创建
 *
 */
package com.global.boot.test.amqp.topic;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yanglie@yiji.com
 */
@Configuration
public class TopicProducer {
	final static String queueNameOne = "spring-boot-topic-one";
	final static String queueNametwo = "spring-boot-topic-two";
	final static String exchangeName = "spring-boot-topic-exchange";
	final static String routingKeyOne = "*.orange.*";
	final static String routingKeyTwo = "*.*.rabbit";
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
	TopicExchange topicExchange() {
		return new TopicExchange(exchangeName);
	}
	
	@Bean
	Binding bindingOne(Queue queueOne, TopicExchange topicExchange) {
		return BindingBuilder.bind(queueOne).to(topicExchange).with(routingKeyOne);
	}
	
	@Bean
	Binding bindingTwo(Queue queueTwo, TopicExchange topicExchange) {
		return BindingBuilder.bind(queueTwo).to(topicExchange).with(routingKeyTwo);
	}
	
	/**
	 * 发送消息
	 */
	public void send(String routingKey, Object object) {
		rabbitTemplate.convertAndSend(exchangeName, routingKey, object);
	}
}
