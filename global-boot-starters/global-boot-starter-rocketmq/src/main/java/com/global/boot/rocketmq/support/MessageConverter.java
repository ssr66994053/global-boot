/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-02-01 15:17 创建
 *
 */
package com.global.boot.rocketmq.support;

import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.global.boot.rocketmq.message.NotifyMessage;

/**
 * @author yanglie@yiji.com
 */
public interface MessageConverter {
	
	Message toMessage(NotifyMessage object) throws MessageConversionException;
	
	NotifyMessage fromMessage(MessageExt message) throws MessageConversionException;
	
}
