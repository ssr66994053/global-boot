/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-25 10:24 创建
 *
 */
package com.global.boot.test.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.annotation.Reference;
import com.global.boot.test.TestBase;
import com.global.common.dubbo.DubboRemoteProxyFacotry;
//import com.yiji.customer.service.api.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author qiubo@yiji.com
 */
public class DubboTest extends TestBase {
	@Reference(version = "1.5")
	private DemoService demoService;
	@Autowired(required = false)
	private ApplicationConfig applicationConfig;
	@Autowired(required = false)
	private RegistryConfig registryConfig;
	@Autowired(required = false)
	private DubboRemoteProxyFacotry dubboRemoteProxyFacotry;
	
//	@Reference(version = "1.5")
//	private UserService userService;
	
//	@Test
//	public void testName() throws Exception {
//		assertThat(demoService).isNotNull();
//		assertThat(applicationConfig).isNotNull();
//		assertThat(registryConfig).isNotNull();
//		assertThat(userService).isNotNull();
//		assertThat(ReflectionTestUtils.getField(dubboRemoteProxyFacotry, "applicationContext")).isNotNull();
//	}
	
	@Test
	public void testName1() throws Exception {
		String message = demoService.echo("xxxx");
		System.out.println(message);
		assertThat(message).contains("xxxx ref:com.alibaba.dubbo.common.bytecode.proxy");
	}

	@Test
	public void testDubboCache() throws Exception {
		//测试缓存put
		assertGetBodyIs("/dubbo/testDubboCache","world");
		//测试缓存get
		assertGetBodyIs("/dubbo/testRedisTemplate","world");
	}
}
