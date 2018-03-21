/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-20 14:14 创建
 *
 */
package com.yiji.boot.test.watcher;

import com.github.kevinsawicki.http.HttpRequest;
import com.yiji.boot.test.TestBase;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author qiubo@yiji.com
 */
public class WatcherWebTest extends TestBase {
	@Test
	public void testWatcher() throws Exception {
		assertThat(HttpRequest.get(buildUrl("/mgt/watcher/")).code()).isEqualTo(HttpStatus.OK.value());
	}
}
