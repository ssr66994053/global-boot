/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-09-09 16:45 创建
 *
 */
package com.yiji.boot.test.security;

import com.github.kevinsawicki.http.HttpRequest;
import com.yiji.boot.test.TestBase;
import com.yiji.common.security.SecurityManager;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author qiubo@yiji.com
 */
public class SecurityTest extends TestBase {
	
	@Autowired(required = false)
	private SecurityManager securityManager;
	
	@Test
	public void testValidSignature() throws Exception {
		String url = buildUrl("sfTest1/testValidSignature.htm?securityUser=ASign&signature=e9d302431d7c37fa315dfb929703e816&v1=asky1&v2=asky2&v3=asky3");
		String result = HttpRequest.get(url).body();
		assertThat(result).isEqualTo("success:验证成功，签名是：e9d302431d7c37fa315dfb929703e816");
	}
	
	@Test
	public void testValidSignature_failure() throws Exception {
		String url = buildUrl("sfTest1/testValidSignature.htm?securityUser=ASign&signature=e9d302431d7c37fa315dfb929703e816&v1=asky1&v2=asky2&v3=asky31");
		String result = HttpRequest.get(url).body();
		Assert.assertTrue(result.indexOf("请求验证签名失败") >= 0);
	}
	
	@Test
	public void testRedirect() throws Exception {
		String url = buildUrl("sfTest1/testRedirect.htm?v1=asky1&v2=asky2&v3=asky3");
		String result = HttpRequest.get(url).body();
		assertThat(result).isEqualTo("success:转换注入成功，内容是：v1 = asky1,v2=asky2,v3=asky3");
	}
	
	@Test
	public void testNeedProtect() throws Exception {
		String url = buildUrl("sfTest1/testNeedProtect.htm?securityUser=AProtect&v1=asky&v2=asky");
		String result = HttpRequest.get(url).body();
		String v1 = new String(this.securityManager.encrypt("AProtect", "asky").getContent());
		assertThat(result).isEqualTo("success:保护成功，内容是：v1 = " + v1 + ",v2=asky");
	}
}
