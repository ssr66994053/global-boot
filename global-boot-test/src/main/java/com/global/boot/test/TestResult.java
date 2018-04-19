/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-15 20:45 创建
 *
 */
package com.global.boot.test;

import com.global.common.lang.result.StandardResultInfo;
import com.global.common.util.ToString;

import java.util.Date;

/**
 * @author qiubo@yiji.com
 */
public class TestResult extends StandardResultInfo {
	private String name;
	
	private Date date;
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
}
