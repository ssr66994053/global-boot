/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2015-09-22 15:07 创建
 *
 */
package com.yiji.boot.test.postman;

import com.yiji.boot.test.TestBase;
import com.yiji.postman.PostmanClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author daidai@yiji.com
 */
public class PostmanTest extends TestBase {
	@Autowired(required = false)
	private PostmanClient postmanClient;
	
	@Test
	public void testSms() {
		assertThat(postmanClient).isNotNull();
	}
}
