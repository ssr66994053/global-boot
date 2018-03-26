/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-09-22 01:37 创建
 *
 */
package com.global.boot.test.shiro;

import com.global.boot.test.TestBase;
import org.junit.Test;

/**
 * @author husheng@yiji.com
 */
public class ShiroTest extends TestBase {
	
	@Test
	public void testShiro() {
		assertGetBodyIs("/shiro", "login");
	}
	
	@Test
	public void testLogin() {
		assertGetBodyIs("/login.html", "login");
	}
	
	@Test
	public void testDoLogin() {
		assertGetBodyIs("/doLogin?userName=abc", "abc success");
	}
	
	@Test
	public void testFilterAlow() {
		assertGetBodyIs("/shiroFilterAlow", "filter");
	}
	
	@Test
	public void testFilterNotAlow() {
		assertGetBodyIs("/shiroFilterNotAlow", "login");
	}
	
}
