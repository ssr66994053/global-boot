/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * zhouxi@yiji.com 2015-03-13 10:21 创建
 *
 */
package com.global.boot.shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author aleishus@yiji.com
 */

public class ShiroRedisCache<K, V> implements Cache<K, V> {
	
	private K cachaName;
	
	private RedisTemplate<K, V> redisTemplate;
	
	public ShiroRedisCache() {
	}
	
	public ShiroRedisCache(K cacheName, RedisTemplate redisTemplate) {
		this.cachaName = cacheName;
		this.redisTemplate = redisTemplate;
	}
	
	@Override
	public V get(K key) throws CacheException {
		HashOperations<K, K, V> hashOperations = redisTemplate.opsForHash();
		return hashOperations.get(cachaName, key);
	}
	
	@Override
	public V put(K key, V value) throws CacheException {
		HashOperations<K, K, V> hashOperations = redisTemplate.opsForHash();
		hashOperations.put(cachaName, key, value);
		redisTemplate.expire(cachaName, 2, TimeUnit.HOURS);
		return hashOperations.get(cachaName, key);
	}
	
	@Override
	public V remove(K key) throws CacheException {
		HashOperations<K, K, V> hashOperations = redisTemplate.opsForHash();
		hashOperations.delete(cachaName, key);
		return null;
	}
	
	@Override
	public void clear() throws CacheException {
		redisTemplate.delete(cachaName);
	}
	
	@Override
	public int size() {
		HashOperations<K, K, V> hashOperations = redisTemplate.opsForHash();
		return hashOperations.size(cachaName).intValue();
	}
	
	@Override
	public Set<K> keys() {
		HashOperations<K, K, V> hashOperations = redisTemplate.opsForHash();
		return hashOperations.keys(cachaName);
	}
	
	@Override
	public Collection<V> values() {
		HashOperations<K, K, V> hashOperations = redisTemplate.opsForHash();
		return hashOperations.values(cachaName);
	}
	
	public RedisTemplate<K, V> getRedisTemplate() {
		return redisTemplate;
	}
	
	public void setRedisTemplate(RedisTemplate<K, V> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
}
