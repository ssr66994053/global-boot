/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-02-05 10:15 创建
 *
 */
package com.yiji.boot.rocketmq.support;

import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.yiji.boot.rocketmq.message.NotifyMessage;
import com.yjf.common.kryo.Kryos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yanglie@yiji.com
 */
public class KyroMessageConverter implements MessageConverter {
	protected final Logger logger = LoggerFactory.getLogger(KyroMessageConverter.class);
	
	@Override
	public Message toMessage(NotifyMessage notifyMessage) throws MessageConversionException {
		try {
			byte[] data = Kryos.serialize(notifyMessage);
			Message message = new Message();
			message.setTopic(notifyMessage.getTopic());
			message.setTags(notifyMessage.getTag());
			message.setBody(data);
			message.setKeys(notifyMessage.getId());
			return message;
		} catch (Exception e) {
			logger.error("使用kryo序列化对象失败");
			throw new MessageConversionException("序列化对象失败", e);
		}
	}
	
	@Override
	public NotifyMessage fromMessage(MessageExt messageExt) throws MessageConversionException {
		if (messageExt == null) {
			return null;
		}
		try {
			NotifyMessage notifyMessage = Kryos.deserialize(messageExt.getBody(), NotifyMessage.class);
			return notifyMessage;
		} catch (Exception e) {
			logger.error("使用kryo反序列化失败.", e);
			throw new MessageConversionException("消息格式不兼容,反序列化失败", e);
		}
	}
}
