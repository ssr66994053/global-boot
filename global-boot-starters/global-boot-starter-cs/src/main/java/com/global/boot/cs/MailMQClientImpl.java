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
import com.yjf.common.kryo.Kryos;
import com.yjf.cs.service.api.mq.MailMQClient;
import com.yjf.cs.service.order.EmailSenderOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.Assert;

/**
 * @author zhouxi@yiji.com
 */
public class MailMQClientImpl implements MailMQClient {
	
	private static final Logger logger = LoggerFactory.getLogger(MailMQClientImpl.class);
	
	private RabbitTemplate template;
	
	/**
	 * 队列名
	 */
	private String routeKey;
	
	public MailMQClientImpl(String routeKey, RabbitTemplate template) {
		this.routeKey = routeKey;
		this.template = template;
	}
	
	/**
	 * 发送邮件
	 *
	 */
	@Override
	public void sendMail(EmailSenderOrder emailSenderOrder) {
		Assert.notNull(emailSenderOrder);
		emailSenderOrder.check();
		logger.info("向CS系统发送异步邮件,邮件gid:{}", emailSenderOrder.getGid());
		//用kryo序列化
		Kryo kryo = Kryos.getKryo();
		Output output = Kryos.getOutput();
		try {
			kryo.writeObject(output, emailSenderOrder);
			Message message = new Message(output.toBytes(), new MessageProperties());
			template.send(routeKey, message);
		} finally {
			output.clear();
		}
	}
}
