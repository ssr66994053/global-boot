package com.yiji.boot.boss.log.manager.impl;

/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * 麦子（lvchen@yiji.com）  2016-05-11 创建
 */

import com.alibaba.dubbo.common.utils.Assert;
import com.yiji.boot.boss.log.domain.BossOperationLogMessage;
import com.yiji.boot.boss.log.manager.BossOperationLogManager;
import com.yjf.common.concurrent.MonitoredThreadPool;
import com.yjf.common.lang.result.StandardResultInfo;
import com.yjf.common.lang.result.Status;
import com.yjf.common.log.Logger;
import com.yjf.common.log.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 麦子（lvchen@yiji.com）
 */
@Component
public class BossOperationLogManagerImpl implements BossOperationLogManager {
	
	private static final Logger logger = LoggerFactory.getLogger(BossOperationLogManagerImpl.class);
	
	@Resource(name = "bossLogMQTemplate")
	private RabbitTemplate bossLogMQTemplate;
	
	@Resource(name = "bossLogThreadPool")
	private MonitoredThreadPool bossLogThreadPool;
	
	@Override
	public StandardResultInfo send(BossOperationLogMessage logMessage) {
		StandardResultInfo resultInfo = new StandardResultInfo();
		try {
			Assert.notNull(logMessage, "日志对象不能为空");
			logMessage.check();
			bossLogThreadPool.execute(() -> {
				bossLogMQTemplate.convertAndSend(logMessage);
				logger.debug("发送数据到日志收集队列成功，{}", logMessage);
			});
			
			resultInfo.setStatus(Status.SUCCESS);
			resultInfo.setDescription("执行成功");
		} catch (IllegalArgumentException e) {
			logger.warn("手动发送数据到日志收集队列失败,参数错误：{}", e.getMessage());
			resultInfo.setStatus(Status.FAIL);
			resultInfo.setDescription("手动发送数据到日志收集队列失败,参数错误：" + e.getMessage());
		} catch (Exception e) {
			logger.error("手动发送数据到日志收集队列失败", e);
			resultInfo.setStatus(Status.FAIL);
			resultInfo.setDescription("手动发送数据到日志收集队列失败，" + e.getMessage());
		}
		return resultInfo;
	}
}
