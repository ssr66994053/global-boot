package com.global.boot.boss.log.manager;

/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * 麦子（lvchen@yiji.com）  2016-05-11 创建
 */

import com.global.boot.boss.log.domain.BossOperationLogMessage;
import com.global.common.lang.result.StandardResultInfo;

/**
 * 提供api接口给子boss系统，手工发送消息到操作队列
 * @author 麦子（lvchen@yiji.com）
 */
public interface BossOperationLogManager {
	
	/**
	 * 手动发送boss操作消息
	 * @param message
	 */
	public StandardResultInfo send(BossOperationLogMessage message);
	
}
