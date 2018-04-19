/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-16 16:01 创建
 */
package com.global.boot.test.dubbo;

import com.alibaba.dubbo.config.annotation.Service;
import com.global.boot.dubbo.cache.RedisCache;
import com.global.common.lang.result.SingleValueResult;
import com.global.common.service.SingleValueOrder;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

/**
 * @author qiubo@yiji.com
 */
@Service(version = "1.5")
@Slf4j
public class CacheableServiceImpl implements CacheableService {
	
	
	private static final Logger log = LoggerFactory.getLogger(CacheableServiceImpl.class);
	
	@Override
	public SingleValueResult<String> echo(SingleValueOrder<String> order) {
		log.info("in method:{}", order.getPlayload());
		return SingleValueResult.from("world");
	}
	@Cacheable(value = CACHE_NAME, key = "#p0.playload")
	public SingleValueResult<String> echoWithSpringCache(SingleValueOrder<String> order) {
		log.info("in method:{}", order.getPlayload());
		return SingleValueResult.from("world");
	}
}
