/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-10-21 16:38 创建
 *
 */
package com.global.boot.test.web;

import com.global.boot.test.TestBase;
import org.junit.Test;

/**
 * @author qiubo@yiji.com
 */
public class VelocityTest extends TestBase {
	@Test
	public void testMacros() throws Exception {
		assertGetBodyContains("/testMacros.html", "<meta name=\"xxxx\" content=\"xxxxx\"/>");
	}
}
