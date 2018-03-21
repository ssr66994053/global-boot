/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-09-15 11:36 创建
 *
 */
package com.yiji.boot.test.amqp.compatible;

import com.yiji.boot.test.TestBase;
import com.yjf.common.kryo.Kryos;
import org.junit.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yanglie@yiji.com
 */
public class CompatibleTest extends TestBase {
	private final static String queueName = "spring-boot-compatible";
	@Autowired(required = false)
	private CompatibleConsumer compatibleConsumer;
	@Autowired(required = false)
	RabbitTemplate rabbitTemplate;
	
	/**
	 * 创建一个名为queueName的队列
	 * @return
	 */
	@Test
	public void testReceive() throws InterruptedException {
		MyMessage myMessage = new MyMessage();
		myMessage.setId("111");
		myMessage.setMessage("测试一把");
		Message message = new Message(Kryos.writeObject(myMessage), new MessageProperties());
		rabbitTemplate.setQueue(queueName);
		rabbitTemplate.setRoutingKey(queueName);
		rabbitTemplate.send(message);
		int i = 0;
		while (i < 3) {
			Thread.sleep(500);
			if (compatibleConsumer.isReady()) {
				break;
			}
		}
		assertThat(compatibleConsumer.isReady()).isTrue();
		assertThat(compatibleConsumer.getLastReceiveMessage().getId()).isEqualTo("111");
	}
}
