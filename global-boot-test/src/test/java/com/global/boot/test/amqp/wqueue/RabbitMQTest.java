/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-27 16:16 创建
 *
 */
package com.global.boot.test.amqp.wqueue;

import com.global.boot.test.TestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yanglie@yiji.com
 */
public class RabbitMQTest extends TestBase {
	@Autowired(required = false)
	private WQProducer WQProducer;
	@Autowired(required = false)
	private Consumer consumer;
	
	@Test
	public void testSendMessage() throws InterruptedException {
		MyMessage myMessage = new MyMessage();
		myMessage.setId("111");
		myMessage.setMessage("hello");
		WQProducer.send(myMessage);
		int i = 0;
		while (i < 3) {
			Thread.sleep(500);
			if (consumer.isReady()) {
				break;
			}
			i++;
		}
		assertThat(consumer.isReady()).isTrue();
		assertThat(consumer.getLastReceiveMessage().getId()).isEqualTo("111");
	}
}
