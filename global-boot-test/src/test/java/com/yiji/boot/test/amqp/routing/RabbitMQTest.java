/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-28 10:46 创建
 *
 */
package com.yiji.boot.test.amqp.routing;

import com.yiji.boot.test.TestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yanglie@yiji.com
 */
public class RabbitMQTest extends TestBase {
	@Autowired(required = false)
	private RoutingProducer routingProducer;
	@Autowired(required = false)
	private RoutingConsumerOne routingConsumerOne;
	@Autowired(required = false)
	private RoutingConsumerTwo routingConsumerTwo;
	
	@Test
	public void testSendMessage() throws InterruptedException {
		MyMessage myMessage1 = new MyMessage();
		myMessage1.setId("111");
		myMessage1.setMessage("hello");
		myMessage1.setRoutingKey("routing.key.one");
		routingProducer.send(myMessage1.getRoutingKey(), myMessage1);
		
		MyMessage myMessage2 = new MyMessage();
		myMessage2.setId("222");
		myMessage2.setMessage("hello");
		myMessage2.setRoutingKey("routing.key.two");
		routingProducer.send(myMessage2.getRoutingKey(), myMessage2);
		
		int i = 0;
		while (i < 3) {
			Thread.sleep(500);
			if (routingConsumerOne.isReady()) {
				break;
			}
		}
		assertThat(routingConsumerOne.isReady()).isTrue();
		assertThat(routingConsumerOne.getLastReceiveMessage().getId()).isEqualTo("111");
		
		i = 0;
		while (i < 3) {
			Thread.sleep(500);
			if (routingConsumerTwo.isReady()) {
				break;
			}
		}
		assertThat(routingConsumerTwo.isReady()).isTrue();
		assertThat(routingConsumerTwo.getLastReceiveMessage().getId()).isEqualTo("222");
	}
}
