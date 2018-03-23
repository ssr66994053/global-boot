/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-10-29 18:27 创建
 *
 */
package com.global.boot.yedis;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;

import javax.annotation.Resource;

/**
 * @author qiubo@yiji.com
 */

public class YedisHealthIndicator extends AbstractHealthIndicator {
	@Resource(name = "redisConnectionFactory")
	private RedisConnectionFactory redisConnectionFactory;
	
	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		RedisConnection connection = RedisConnectionUtils.getConnection(this.redisConnectionFactory);
		try {
			connection.ping();
			builder.up();
		} finally {
			RedisConnectionUtils.releaseConnection(connection, this.redisConnectionFactory);
		}
	}
}
