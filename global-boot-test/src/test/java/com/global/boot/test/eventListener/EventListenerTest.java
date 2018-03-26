/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-10-06 18:01 创建
 *
 */
package com.global.boot.test.eventListener;

import com.global.boot.test.TestBase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

/**
 * @author qiubo@yiji.com
 */
public class EventListenerTest extends TestBase {
	private static final Logger logger = LoggerFactory.getLogger(EventListenerTest.class);
	
	@Autowired(required = false)
	private ApplicationEventPublisher publisher;
	
	@Test
	public void testPublish() throws Exception {
		publisher.publishEvent(new EventListeners.PoEvent("xxxx"));
		publisher.publishEvent(new EventListeners.PoEvent("xxxx"));
	}
	
}
