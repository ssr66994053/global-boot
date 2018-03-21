/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-18 16:00 创建
 */
package com.yiji.boot.dubbo.cache;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.SpringVersion;

/**
 * @author qiubo@yiji.com
 */
@Activate(group = { Constants.CONSUMER }, order = Integer.MIN_VALUE + 1)
public class ConsumerCacheFilter implements Filter {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private CacheFactory cacheFactory = new RedisCacheFactory();
	
	private volatile KeyGenerator keyGenerator;
	private volatile Boolean support;
	
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		if (support == null) {
			//spring 3版本不支持
			if (SpringVersion.getVersion().startsWith("3")) {
				support = false;
			} else {
				support = true;
				keyGenerator = new SpelKeyGenerator();
			}
		}
		if (support) {
			RedisCache cache = cacheFactory.getCache(invoker, invocation);
			if (cache != null && cache != NullCache.INSTANCE) {
				Object key = keyGenerator.key(cache.getCacheMeta(), invocation.getArguments());
				if (cache.isKeySupported(key)) {
					Object value = cache.get(key);
					if (value != null) {
						logger.info("@DubboCache hit,service={},key={},result={}",
							cache.getCacheMeta().getMethodFullName(), key, value);
						return new RpcResult(value);
					}
					Result result = invoker.invoke(invocation);
					if (!result.hasException()) {
						if (cache.getValidator().isValid(invoker.getUrl(), invocation, result.getValue())) {
							cache.put(key, result.getValue());
						}
					}
					return result;
				} else {
					logger.error(
						"key type " + key.getClass().getName() + " is not supported by " + cache.getClass().getName());
				}
			}
		}
		return invoker.invoke(invocation);
	}
	
}
