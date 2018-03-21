/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-10-23 14:38 创建
 *
 */
package com.yiji.boot.test.other;

import com.google.common.collect.Lists;
import org.junit.Test;

/**
 * @author qiubo@yiji.com
 */
public class ParametersTest {
	@Test
	public void testName() throws Exception {
		Lists.newArrayList(TB.class.getDeclaredMethod("test", String.class).getParameters()).forEach(System.out::print);
	}
	
	static class TB {
		public void test(String param) {
			
		}
	}
}
