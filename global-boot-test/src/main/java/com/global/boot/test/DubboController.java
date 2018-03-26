/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-15 14:12 创建
 *
 */
package com.global.boot.test;

import com.alibaba.dubbo.config.annotation.Reference;
import com.global.boot.test.dubbo.CacheableService;
import com.global.boot.test.dubbo.CacheableServiceImpl;
import com.global.boot.test.dubbo.DemoService;
import com.yjf.common.id.GID;
import com.yjf.common.lang.result.SingleValueResult;
import com.yjf.common.service.SingleValueOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.global.boot.test.dubbo.CacheableService.CACHE_NAME;

/**
 * @author qiubo@yiji.com
 */
@Controller
public class DubboController {
	
	@Reference(version = "1.5")
	private DemoService demoService;
	@Reference(version = "1.5")
	private CacheableService cacheableService;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private CacheableServiceImpl cacheableServiceImpl;
	
	@RequestMapping(value = "/testDubbo")
	@ResponseBody
	public String testDubbot() throws Exception {
		SingleValueOrder singleValueOrder = new SingleValueOrder();
		singleValueOrder.setPlayload("xx");
		singleValueOrder.setGid(GID.newGID());
		return (String) demoService.echo1(singleValueOrder).getPlayload();
	}
	
	@RequestMapping(value = "/dubbo/testDubboCache")
	@ResponseBody
	public String testCache() throws Exception {
		SingleValueOrder singleValueOrder = new SingleValueOrder();
		singleValueOrder.setPlayload("xx");
		singleValueOrder.setGid(GID.newGID());
		return (String) cacheableService.echo(singleValueOrder).getPlayload();
	}
	
	@RequestMapping(value = "/dubbo/testRedisTemplate")
	@ResponseBody
	public String testRedisTemplate() throws Exception {
		SingleValueResult<String> o = (SingleValueResult<String>)redisTemplate.opsForValue().get(CACHE_NAME + ":xx");
		return o.getPlayload();
	}
	
	@RequestMapping(value = "/dubbo/testSpringCache")
	@ResponseBody
	public void testSpringCache() throws Exception {
		SingleValueOrder singleValueOrder = new SingleValueOrder();
		singleValueOrder.setPlayload("xx");
		singleValueOrder.setGid(GID.newGID());
		cacheableServiceImpl.echoWithSpringCache(singleValueOrder);
	}
}
