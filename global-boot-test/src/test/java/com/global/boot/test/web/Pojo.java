/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-09-16 21:24 创建
 *
 */
package com.global.boot.test.web;

import com.yjf.common.lang.util.money.Money;

import java.util.Date;

/**
 * @author qiubo@yiji.com
 */
public class Pojo {
	private String name;
	private Date date;
	private Money money;
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public Money getMoney() {
		return money;
	}
	
	public void setMoney(Money money) {
		this.money = money;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
