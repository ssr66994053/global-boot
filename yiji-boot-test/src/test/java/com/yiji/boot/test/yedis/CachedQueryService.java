/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-21 15:07 创建
 *
 */
package com.yiji.boot.test.yedis;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author yanglie@yiji.com
 */
@Service
public class CachedQueryService {
	private Map<String, Object> dataMap = new HashedMap();
	
	@Cacheable(value = "test", key = "#name")
	public Object getValueByName(String name) {
		System.out.println("find name from cache + [" + name + "]");
		Object value = dataMap.get(name);
		if (value == null) {
			value = "test_value_nocache";
		}
		return value;
	}
	
	@CacheEvict(value = "test", key = "#name")
	public void deleteValue(String name) {
		dataMap.remove(name);
		System.out.println("remove cache " + "[" + name + "]");
	}
	
	@CachePut(value = "test", key = "#name")
	public Object saveValue(String name, Object value) {
		System.out.println("add cache " + "[" + name + "," + value + "]");
		dataMap.put(name, value);
		return value + "_cache";
	}
}
