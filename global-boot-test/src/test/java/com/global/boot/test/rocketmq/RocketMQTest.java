/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-02-15 09:40 创建
 *
 */
package com.global.boot.test.rocketmq;

import com.google.common.collect.Sets;
import com.global.boot.rocketmq.message.NotifyMessage;
import com.global.boot.rocketmq.message.OrderedNotifyMessage;
import com.global.boot.rocketmq.producer.MessageProducer;
import com.global.boot.rocketmq.producer.MessageResult;
import com.global.boot.test.TestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yanglie@yiji.com
 */
public class RocketMQTest extends TestBase {
	@Autowired(required = false)
	MessageProducer messageProducer;
	@Autowired(required = false)
	RocketConsumer rocketConsumer;
	
	@Test
	public void testSendMessage() throws Exception {
		Map<String, Object> data = new HashMap<>();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
		String timeStr = df.format(new Date());
		data.put("key", "value");
		Set<String> sendIds = Sets.newHashSet();
		for (int i = 0; i < 3; i++) {
			OrderedNotifyMessage message = new OrderedNotifyMessage();
			message.setGroupId("order_001");
			message.setTopic("TopicTest");
			message.setEvent("event_trade");
			message.setToSystem("yiji-boot-test");
			String id = "trade_ordered_" + timeStr + "_" + i;
			message.setId(id);
			message.setGid("0123456789012345678901234567891234" + i);
			message.setData(data);
			
			MessageResult result = messageProducer.send(message);
			if (result.isSuccess()) {
				sendIds.add(id);
			}
			
			NotifyMessage mgs = new NotifyMessage();
			mgs.setTopic("TopicTest");
			mgs.setEvent("event_trade");
			mgs.setToSystem("cashier");
			id = "trade_" + timeStr + "_" + i;
			mgs.setId(id);
			mgs.setGid("0123456789012345678901234567891234" + i);
			mgs.setData(data);
			result = messageProducer.send(mgs);
			if (result.isSuccess()) {
				sendIds.add(id);
			}
		}
		for (int i = 0; i < 100; i++) {
			Thread.sleep(100);
			if (sendIds.equals(rocketConsumer.getIds())) {
				break;
			}
		}
		assertThat(sendIds).isEqualTo(rocketConsumer.getIds());
	}
}
