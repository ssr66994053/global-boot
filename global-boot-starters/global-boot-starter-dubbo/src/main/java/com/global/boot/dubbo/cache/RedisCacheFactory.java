/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-18 16:07 创建
 */
package com.global.boot.dubbo.cache;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.global.boot.core.Apps;
import com.global.boot.core.components.dubbo.DubboCache;
import com.global.boot.yedis.YedisProperties;
import com.global.framework.yedis.support.YedisConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;

/**
 * @author qiubo@yiji.com
 */
@Slf4j
public class RedisCacheFactory implements CacheFactory {
	private static ConcurrentMap<CacheMeta, RedisCache> concurrentMap = Maps.newConcurrentMap();
	private boolean redisInited;
	private RedisTemplate redisTemplate;
	
	@Override
	public RedisCache getCache(Invoker<?> invoker, Invocation inv) {
		try {
			if (redisTemplate == null) {
				if (redisInited) {
					return NullCache.INSTANCE;
				}
				initRedisTemplate();
			}
			if (redisTemplate == null) {
				return NullCache.INSTANCE;
			}
			
			String group = invoker.getUrl().getParameter(Constants.GROUP_KEY);
			if (!Strings.isNullOrEmpty(group)) {
				return NullCache.INSTANCE;
			}
			String application = invoker.getUrl().getParameter(Constants.APPLICATION_KEY);
			Class interf = invoker.getInterface();
			String methodName = inv.getMethodName();
			Class[] argsClass = inv.getParameterTypes();
			
			Method method = interf.getDeclaredMethod(methodName, argsClass);
			DubboCache dubboCache = method.getAnnotation(DubboCache.class);
			if (dubboCache == null) {
				return NullCache.INSTANCE;
			}
			CacheMeta cacheMeta = new CacheMeta();
			cacheMeta.setMethod(method);
			cacheMeta.setDubboCache(dubboCache);
			cacheMeta.setParameterTypes(argsClass);
			cacheMeta.setTargetClass(interf);
			String namespace;
			if (dubboCache.namespace().equals("")) {
				namespace = YedisProperties.buidCacheNameSpace(application);
			} else {
				namespace = dubboCache.namespace();
			}
			cacheMeta.setNamespace(namespace);
			RedisCache redisCache = concurrentMap.get(cacheMeta);
			if (redisCache != null) {
				return redisCache;
			} else {
				redisCache = new RedisCache(redisTemplate, cacheMeta);
				concurrentMap.putIfAbsent(cacheMeta, redisCache);
				return redisCache;
			}
		} catch (Exception e) {
			log.warn("create RedisCacheFactory failure", e);
			return NullCache.INSTANCE;
		}
	}
	
	private void initRedisTemplate() {
		synchronized (this) {
			if (redisTemplate == null) {
				redisInited = true;
				try {
					ApplicationContext applicationContext = Apps.getApplicationContext();
					if (applicationContext == null) {
						log.warn("非yiji-boot应用，缓存不生效");
						return;
					}
					RedisTemplate template = Apps.getApplicationContext().getBean("redisTemplate", RedisTemplate.class);
					RedisConnectionFactory redisConnectionFactory = template.getConnectionFactory();
					if (!(redisConnectionFactory instanceof YedisConnectionFactory)) {
						log.warn("redisTemplate 没有使用yedis，缓存不生效");
						return;
					}
					redisTemplate = template;
				} catch (BeansException e) {
					log.warn("dubbo client cache failure", e);
				}
			}
		}
	}
}
