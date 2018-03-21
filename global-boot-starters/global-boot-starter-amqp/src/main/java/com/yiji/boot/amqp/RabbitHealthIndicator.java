/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-05-23 17:08 创建
 */
package com.yiji.boot.amqp;

import com.yiji.boot.core.EnvironmentHolder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author qiubo@yiji.com
 */
public class RabbitHealthIndicator extends AbstractHealthIndicator {
	
	private final RabbitTemplate rabbitTemplate;
	
	public RabbitHealthIndicator(RabbitTemplate rabbitTemplate) {
		Assert.notNull(rabbitTemplate, "RabbitTemplate must not be null.");
		this.rabbitTemplate = rabbitTemplate;
	}
	
	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		builder.up().withDetail("version", getVersion(builder));
	}
	
	private String getVersion(Health.Builder builder) {
		try {
			return this.rabbitTemplate.execute(channel -> {
				Map<String, Object> serverProperties = channel.getConnection().getServerProperties();
				return serverProperties.get("version").toString();
			});
		} catch (Exception e) {
			String username = EnvironmentHolder.get().getProperty("spring.rabbitmq.username");
			String host = EnvironmentHolder.get().getProperty("spring.rabbitmq.host");
			String port = EnvironmentHolder.get().getProperty("spring.rabbitmq.port");
			builder.withDetail("url", username + "@" + host + ":" + port);
			throw e;
		}
	}
	
}
