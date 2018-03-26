/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-28 10:19 创建
 *
 */
package com.global.boot.test.amqp.routing;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yanglie@yiji.com
 */
@Configuration
public class RoutingProducer {
	final static String queueNameOne = "spring-boot-routing-one";
	final static String queueNametwo = "spring-boot-routing-two";
	final static String exchangeName = "spring-boot-routing-exchange";
	final static String routingKeyOne = "routing.key.one";
	final static String routingKeyTwo = "routing.key.two";
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
	DirectExchange directExchange() {
		return new DirectExchange(exchangeName);
	}
	
	@Bean
	Binding bindingOne(Queue queueOne, DirectExchange directExchange) {
		return BindingBuilder.bind(queueOne).to(directExchange).with(routingKeyOne);
	}
	
	@Bean
	Binding bindingTwo(Queue queueTwo, DirectExchange directExchange) {
		return BindingBuilder.bind(queueTwo).to(directExchange).with(routingKeyTwo);
	}
	
	/**
	 * 发送消息
	 */
	public void send(String routingKey, Object object) {
		rabbitTemplate.convertAndSend(exchangeName, routingKey, object);
	}
}
