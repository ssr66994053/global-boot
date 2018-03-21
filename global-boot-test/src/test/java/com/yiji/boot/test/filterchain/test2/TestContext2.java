/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-02-18 18:46 创建
 */
package com.yiji.boot.test.filterchain.test2;

import com.yiji.boot.filterchain.Context;

/**
 * @author qiubo@yiji.com
 */
public class TestContext2 extends Context {
	private String name;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
