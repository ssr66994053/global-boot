/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 *
 * 修订记录:
 * zhouxi@yiji.com 2015-09-15 15:41 创建
 *
 */
package com.yiji.boot.cs;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

/**
 * @author zhouxi@yiji.com
 */
@ConfigurationProperties("yiji.cs")
public class CSProperties implements InitializingBean {
	
	/**
	 * 是否启用cs组件
	 */
	private boolean enable = true;
	
	/**
	 * 必填: shuntMessage queue name
	 */
	private String shuntMessageQueueName;
	
	/**
	 * 必填: email queue name
	 */
	private String emailQueueName;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (enable) {
			Assert.hasText(shuntMessageQueueName, "异步消息队列名字不能为空");
			Assert.hasText(emailQueueName, "邮件队列名字不能为空");
		}
	}
	
	public String getShuntMessageQueueName() {
		return shuntMessageQueueName;
	}
	
	public void setShuntMessageQueueName(String shuntMessageQueueName) {
		this.shuntMessageQueueName = shuntMessageQueueName;
	}
	
	public String getEmailQueueName() {
		return emailQueueName;
	}
	
	public void setEmailQueueName(String emailQueueName) {
		this.emailQueueName = emailQueueName;
	}
	
	public boolean isEnable() {
		return enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
}
