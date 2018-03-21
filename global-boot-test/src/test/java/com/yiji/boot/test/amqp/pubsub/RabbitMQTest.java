/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-28 10:46 创建
 *
 */
package com.yiji.boot.test.amqp.pubsub;

import com.yiji.boot.test.TestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yanglie@yiji.com
 */
public class RabbitMQTest extends TestBase {
	@Autowired(required = false)
	private PSProducer psProducer;
	@Autowired(required = false)
	private PSConsumerOne psConsumerOne;
	@Autowired(required = false)
	private PSConsumerOne psConsumerTwo;
	
	@Test
	public void testSendMessage() throws InterruptedException {
		MyMessage myMessage = new MyMessage();
		myMessage.setId("111");
		myMessage.setMessage("hello");
		psProducer.send(myMessage);
		int i = 0;
		while (i < 3) {
			Thread.sleep(500);
			if (psConsumerOne.isReady()) {
				break;
			}
		}
		assertThat(psConsumerOne.isReady()).isTrue();
		assertThat(psConsumerOne.getLastReceiveMessage().getId()).isEqualTo("111");
		
		i = 0;
		while (i < 3) {
			Thread.sleep(500);
			if (psConsumerTwo.isReady()) {
				break;
			}
		}
		assertThat(psConsumerTwo.isReady()).isTrue();
		assertThat(psConsumerTwo.getLastReceiveMessage().getId()).isEqualTo("111");
	}
}
