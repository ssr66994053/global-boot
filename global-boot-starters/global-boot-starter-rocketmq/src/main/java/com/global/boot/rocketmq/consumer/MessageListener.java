/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-02-01 15:16 创建
 *
 */
package com.global.boot.rocketmq.consumer;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.common.message.MessageExt;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author yanglie@yiji.com
 */
public interface MessageListener {
	void onMessage(List<MessageExt> messages, ConsumeConcurrentlyContext context) throws InvocationTargetException,
																					IllegalAccessException;
}
