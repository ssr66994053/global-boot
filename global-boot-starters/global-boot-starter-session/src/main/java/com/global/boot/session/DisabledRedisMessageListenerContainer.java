/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-26 16:48 创建
 *
 */
package com.global.boot.session;

import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * 禁用spring session redis 消息
 * @author qiubo@yiji.com
 */
public class DisabledRedisMessageListenerContainer extends RedisMessageListenerContainer {
	
	private volatile boolean running = false;
	
	@Override
	public void afterPropertiesSet() {
		
	}
	
	@Override
	public void start() {
		running = true;
	}
	
	@Override
	public void stop() {
		running = false;
		
	}
	
	@Override
	public void stop(Runnable callback) {
		callback.run();
		this.stop();
	}
	
	@Override
	public void destroy() throws Exception {
		this.stop();
	}
	
	@Override
	public boolean isRunning() {
		return running;
	}
}
