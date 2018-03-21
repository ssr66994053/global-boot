/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-18 16:10 创建
 */
package com.yiji.boot.dubbo.cache;

import com.alibaba.dubbo.cache.support.AbstractCache;
import com.google.common.base.Strings;
import com.yiji.framework.yedis.support.YedisConnectionFactory;
import com.yjf.common.lang.result.ResultInfo;
import com.yjf.common.lang.result.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.cache.DefaultRedisCachePrefix;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;
import java.util.function.BiFunction;

/**
 * @author qiubo@yiji.com
 */
@Slf4j
public class RedisCache extends AbstractCache {
	private RedisTemplate<String, Object> redisTemplate;
	private long expirationSecs;
	private CacheMeta cacheMeta;
	private byte[] namespace;
	private YedisConnectionFactory yedisConnectionFactory;
	private RedisSerializer valueRedisSerializer;
	private byte[] prefix;
	private StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
	
	public RedisCache(RedisTemplate redisTemplate, CacheMeta cacheMeta) {
		this.redisTemplate = redisTemplate;
		this.valueRedisSerializer = redisTemplate.getValueSerializer();
		this.expirationSecs = cacheMeta.getDubboCache().expire();
		this.cacheMeta = cacheMeta;
		this.setValidator((url, invocation, value) -> {
			if (value == null) {
				return false;
			}
			if (value instanceof ResultInfo) {
				return ((ResultInfo) value).getStatus() == Status.SUCCESS;
			}
			return true;
		});
		this.namespace = cacheMeta.getNamespace().getBytes();
		this.yedisConnectionFactory = (YedisConnectionFactory) redisTemplate.getConnectionFactory();
		String cacheName = cacheMeta.getDubboCache().cacheName();
		if (Strings.isNullOrEmpty(cacheName)) {
			cacheName = "";
		}
		this.prefix = new DefaultRedisCachePrefix().prefix(cacheName);
	}

	RedisCache() {
	}

	@Override
	public void put(Object key, Object value) {
		doit((String) key, (conn, newkey) -> {
			conn.set(newkey, valueRedisSerializer.serialize(value));
			if (expirationSecs > 0) {
				conn.expire(newkey, expirationSecs);
			}
			return null;
		});
	}
	
	@Override
	public Object get(Object key) {
		return doit((String) key, (conn, newkey) -> {
			byte[] bytes = conn.get(newkey);
			if (bytes == null) {
				return null;
			}
			return valueRedisSerializer.deserialize(bytes);
		});
	}
	
	public Object doit(String key, BiFunction<RedisConnection, byte[], Object> fun) {
		RedisConnection connection = null;
		try {
			connection = yedisConnectionFactory.getConnectionWithoutNS();
			byte[] newkey = computeKey(key);
			return fun.apply(connection, newkey);
		} catch (Exception ex) {
			log.warn("dubbo access cache failure", ex);
			return null;
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
	
	private byte[] computeKey(Object key) {
		byte[] keyBytes = this.convertToBytesIfNecessary(stringRedisSerializer, key);
		byte[] result = Arrays.copyOf(this.namespace, this.namespace.length + keyBytes.length + prefix.length);
		System.arraycopy(prefix, 0, result, this.namespace.length, prefix.length);
		System.arraycopy(keyBytes, 0, result, this.namespace.length + prefix.length, keyBytes.length);
		return result;
		
	}
	
	private byte[] convertToBytesIfNecessary(StringRedisSerializer serializer, Object value) {
		return value instanceof byte[] ? (byte[]) (value)
			: (null == serializer ? null : serializer.serialize((String) value));
	}
	
	public CacheMeta getCacheMeta() {
		return cacheMeta;
	}
}
