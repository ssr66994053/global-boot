/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-21 13:10 创建
 *
 */
package com.yiji.boot.test.yedis;

import com.yiji.boot.test.TestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yanglie@yiji.com
 */
public class YedisCacheTest extends TestBase {
	@Autowired(required = false)
	private RedisTemplate redisTemplate;
	@Autowired(required = false)
	private CachedQueryService cachedQueryService;
	
	/**
	 * 测试yedis功能是否生效
	 */
	@Test
	public void testYedisOperation() {
		redisTemplate.opsForValue().set("yedis.boot.test", "test");
		String value = (String) redisTemplate.opsForValue().get("yedis.boot.test");
		assertThat("test").isEqualTo(value);
		redisTemplate.delete("yedis.boot.test");
		Object res = redisTemplate.opsForValue().get("yedis.boot.test");
		assertThat(res).isNull();
	}
	
	/**
	 * 测试cacheManager功能是否生效
	 */
	@Test
	public void testCacheManager() {
		//新增缓冲
		cachedQueryService.saveValue("yanglie", "test_value");
		//从缓存中获取,Service的getValueByName的方法体不会执行
		Object cacheValue = cachedQueryService.getValueByName("yanglie");
		assertThat("test_value_cache").isEqualTo(cacheValue);
		//删除缓存和数据
		cachedQueryService.deleteValue("yanglie");
		//会执行getValueByName的方法体
		cacheValue = cachedQueryService.getValueByName("yanglie");
		assertThat("test_value_nocache").isEqualTo(cacheValue);
		
		cachedQueryService.deleteValue("yanglie");
		
	}
}
