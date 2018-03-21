/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-10-06 18:22 创建
 *
 */
package com.yiji.boot.test.eventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author qiubo@yiji.com
 */
@Component
public class EventListeners {
	private static final Logger logger = LoggerFactory.getLogger(EventListeners.class);
	
	@EventListener
	public void handle(PoEvent poEvent) {
		logger.info("{}", poEvent);
	}
	
	public static class PoEvent {
		private String msg;
		
		public PoEvent(String msg) {
			this.msg = msg;
		}
		
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("PoEvent{");
			sb.append("msg='").append(msg).append('\'');
			sb.append('}');
			return sb.toString();
		}
	}
}
