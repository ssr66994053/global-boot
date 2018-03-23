/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 *
 * 修订记录:
 * zhouxi@yiji.com 2015-09-15 15:41 创建
 *
 */
package com.yiji.boot.cs;

import com.yjf.cs.service.api.mq.MailMQClient;
import com.yjf.cs.service.api.mq.ShuntMQClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhouxi@yiji.com
 */

@Configuration
@ConditionalOnProperty(value = "yiji.cs.enable", matchIfMissing = true)
@EnableConfigurationProperties({ CSProperties.class })
public class CSAutoConfiguration {
	
	@Autowired
	private CSProperties csProperties;
	
	@Bean
	public ShuntMQClient shuntMQClient(RabbitTemplate rabbitTemplate) {
		ShuntMQClient shuntMQClient = new ShuntMQClientImpl(rabbitTemplate, csProperties.getShuntMessageQueueName());
		return shuntMQClient;
		
	}
	
	@Bean
	public MailMQClient mailMQClient(RabbitTemplate rabbitTemplate) {
		MailMQClient mailMQClient = new MailMQClientImpl(csProperties.getEmailQueueName(), rabbitTemplate);
		return mailMQClient;
	}
}
