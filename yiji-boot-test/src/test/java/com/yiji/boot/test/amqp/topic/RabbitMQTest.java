/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-28 10:46 创建
 *
 */
package com.yiji.boot.test.amqp.topic;

import com.yiji.boot.test.TestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yanglie@yiji.com
 */
public class RabbitMQTest extends TestBase {
	@Autowired(required = false)
	private TopicProducer topicProducer;
	@Autowired(required = false)
	private TopicConsumerOne topicConsumerOne;
	@Autowired(required = false)
	private TopicConsumerTwo topicConsumerTwo;
	
	@Test
	public void testSendMessage() throws InterruptedException {
		MyMessage myMessage1 = new MyMessage();
		myMessage1.setId("111");
		myMessage1.setMessage("hello");
		myMessage1.setRoutingKey("quick.orange.fox");
		topicProducer.send(myMessage1.getRoutingKey(), myMessage1);
		
		MyMessage myMessage2 = new MyMessage();
		myMessage2.setId("222");
		myMessage2.setMessage("hello");
		myMessage2.setRoutingKey("lazy.pink.rabbit");
		topicProducer.send(myMessage2.getRoutingKey(), myMessage2);
		
		int i = 0;
		while (i < 3) {
			Thread.sleep(500);
			if (topicConsumerOne.isReady()) {
				break;
			}
		}
		assertThat(topicConsumerOne.isReady()).isTrue();
		assertThat(topicConsumerOne.getLastReceiveMessage().getId()).isEqualTo("111");
		
		i = 0;
		while (i < 3) {
			Thread.sleep(500);
			if (topicConsumerTwo.isReady()) {
				break;
			}
		}
		assertThat(topicConsumerTwo.isReady()).isTrue();
		assertThat(topicConsumerTwo.getLastReceiveMessage().getId()).isEqualTo("222");
	}
}
