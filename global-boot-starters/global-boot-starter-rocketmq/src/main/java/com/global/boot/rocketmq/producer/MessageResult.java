/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-02-22 16:44 创建
 *
 */
package com.global.boot.rocketmq.producer;

import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;

/**
 * @author yanglie@yiji.com
 */
public class MessageResult {
	private SendResult sendResult;
	
	public MessageResult(SendResult sendResult) {
		this.sendResult = sendResult;
	}
	
	public boolean isSuccess() {
		return sendResult.getSendStatus() == SendStatus.SEND_OK;
	}
	
	public boolean isFail() {
		return sendResult.getSendStatus() != SendStatus.SEND_OK;
	}
	
	public void setSendResult(SendResult sendResult) {
		this.sendResult = sendResult;
	}
	
	public SendResult getSendResult() {
		return this.sendResult;
	}
}
