/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-27 14:27 创建
 *
 */
package com.yiji.boot.amqp;

import com.yjf.common.kryo.Kryos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * @author yanglie@yiji.com
 */
public class KyroMessageConverter implements MessageConverter {
	protected final Logger logger = LoggerFactory.getLogger(KyroMessageConverter.class);
	
	@Override
	public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
		if (messageProperties == null) {
			messageProperties = new MessageProperties();
		}
		try {
			byte[] data = Kryos.serialize(object);
			Message message = new Message(data, messageProperties);
			return message;
		} catch (Exception e) {
			logger.error("使用kryo序列化对象失败");
			throw new MessageConversionException("序列化对象失败", e);
		}
	}
	
	@Override
	public Object fromMessage(Message message) throws MessageConversionException {
		if (message == null) {
			return message;
		}
		try {
			Object object = Kryos.deserialize(message.getBody());
			return object;
		} catch (Exception e) {
			logger.error("使用kryo反序列化失败.", e);
			throw new MessageConversionException("消息格式不兼容,反序列化失败", e);
		}
	}
}
