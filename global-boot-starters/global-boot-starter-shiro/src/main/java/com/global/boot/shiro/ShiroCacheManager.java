package com.yiji.boot.shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;
import org.springframework.data.redis.core.RedisTemplate;

public class ShiroCacheManager implements CacheManager, Destroyable {
	
	public static final String KEY_PREFIX = "shiro_redis_cache:";
	public static final String KEY_AUTHZ = "authorizationCache";
	public static final String KEY_AUTHC = "authenticationCache";
	
	@Override
	public <K, V> Cache<K, V> getCache(String name) throws CacheException {
		
		return new ShiroRedisCache(KEY_PREFIX + name, redisTemplate);
	}
	
	@Override
	public void destroy() throws Exception {
		redisTemplate.delete(KEY_PREFIX + KEY_AUTHZ);
		redisTemplate.delete(KEY_PREFIX + KEY_AUTHC);
	}
	
	private RedisTemplate redisTemplate;
	
	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	
}
