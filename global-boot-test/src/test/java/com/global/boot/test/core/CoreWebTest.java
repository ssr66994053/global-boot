/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-20 14:19 创建
 *
 */
package com.global.boot.test.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.global.boot.test.TestBase;
import org.junit.Test;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author qiubo@yiji.com
 */
public class CoreWebTest extends TestBase {
	@Test
	public void testHera() throws Exception {
		ResponseEntity<String> entity = new TestRestTemplate().getForEntity(buildUrl("/testHera.json"), String.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		JSONObject jsonObject = JSON.parseObject(entity.getBody());
		assertThat(jsonObject.get("valueFormHera")).isNotNull();
		assertThat(jsonObject.get("valueFormHera1")).isNotNull();
	}
}
