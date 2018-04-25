/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 *
 * 修订记录:
 * zhouxi@yiji.com 2015-09-15 15:41 创建
 *
 */
package com.global.boot.cs;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.global.common.kryo.Kryos;
import com.yjf.cs.service.api.mq.ShuntMQClient;
import com.yjf.cs.service.order.ShunMessageOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.Assert;

/**
 * @author zhouxi@yiji.com
 */
public class ShuntMQClientImpl implements ShuntMQClient {
	
	private Logger logger = LoggerFactory.getLogger(ShuntMQClientImpl.class);
	
	private RabbitTemplate template;
	
	/**
	 * 队列名
	 */
	private String routeKey;
	
	public ShuntMQClientImpl(RabbitTemplate template, String routeKey) {
		this.template = template;
		this.routeKey = routeKey;
	}
	
	@Override
	public void notify(ShunMessageOrder shunMessageOrder) {
		Assert.notNull(shunMessageOrder);
		shunMessageOrder.check();
		logger.info("开始向CS系统发送异步消息,消息gid:{}", shunMessageOrder.getGid());
		Kryo kryo = Kryos.getKryo();
		Output output = Kryos.getOutput();
		try {
			kryo.writeObject(output, shunMessageOrder);
			Message message = new Message(output.toBytes(), new MessageProperties());
			template.send(routeKey, message);
		} finally {
			output.clear();
		}
	}
}
