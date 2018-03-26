/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-02-18 18:46 创建
 */
package com.global.boot.test.filterchain.test1;

import com.google.common.collect.Lists;
import com.global.boot.filterchain.Context;

import java.util.List;

/**
 * @author qiubo@yiji.com
 */
public class TestContext1 extends Context {
	private String name = "";
	
	private List<String> msgs = Lists.newArrayList();
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<String> getMsgs() {
		return msgs;
	}
	
	public void setMsgs(List<String> msg) {
		this.msgs = msg;
	}
	
	public void addMsg(String msg) {
		this.msgs.add(msg);
	}
}
