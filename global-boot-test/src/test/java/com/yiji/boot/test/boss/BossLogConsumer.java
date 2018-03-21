package com.yiji.boot.test.boss;

/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * 麦子（lvchen@yiji.com）  2016-04-26 创建
 */

import com.yiji.boot.boss.log.domain.BossOperationLogMessage;
import com.yjf.common.log.Logger;
import com.yjf.common.log.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author 麦子（lvchen@yiji.com）
 */
@Component
public class BossLogConsumer {
	
	private Logger logger = LoggerFactory.getLogger(BossLogConsumer.class);
	private final static String queueName = "boss.log";
	private volatile boolean ready = false;
	private BossOperationLogMessage bossOperationLogMessage;
	
	/**
	 * 定义一个消息的监听处理方法，会接收到queueName的上的消息，并进行处理
	 * @param message
	 */
	@RabbitListener(queues = queueName)
	public void process(BossOperationLogMessage message) {
		logger.info("收到的消息：{}", message);
		this.bossOperationLogMessage = message;
		ready = true;
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	
	public BossOperationLogMessage getLastReceiveMessage() {
		return bossOperationLogMessage;
	}
	
	public void setLastReceiveMessage(BossOperationLogMessage lastReceiveMessage) {
		this.bossOperationLogMessage = lastReceiveMessage;
	}
}
