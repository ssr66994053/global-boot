/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-10-11 23:59 创建
 */
package com.global.boot.test.appservice;

import com.global.common.lang.enums.CommonErrorCode;
import com.global.common.lang.result.SingleValueResult;
import com.global.common.lang.result.Status;
import com.global.common.lang.validator.Validators;
import com.global.common.service.OrderCheckException;
import com.global.common.service.SingleValueOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.groups.Default;

/**
 * 用于性能对比测试
 *
 * @author qiubo@yiji.com
 */
@Service
public class CommonServiceTestService {
	private static final Logger logger = LoggerFactory.getLogger(AppServiceTestContorller.class);
	
	public SingleValueResult<AppDto> test1(SingleValueOrder<AppDto> appRequest) {
		long begin = System.currentTimeMillis();
		logger.info("[{}]请求入参:{}", "AppServiceTestContorller#test4", appRequest);
		
		SingleValueResult<AppDto> response = new SingleValueResult<AppDto>();
		try {
			Validators.checkJsr303(appRequest, Default.class, AppDto.Test1.class);
			//do what you like
			response.setPlayload(appRequest.getPlayload());
		} catch (OrderCheckException e) {
			appRequest.getPlayload().checkOnTest1(e);
			logger.error("处理异常:", e);
			response.setDescription(e.getMessage());
			response.setStatus(Status.FAIL);
			response.setCode(CommonErrorCode.INVALID_ARGUMENTS.code());
		}
		logger.info("[{}]请求响应:{},耗时:{}ms", "AppServiceTestContorller#test4", response, System.currentTimeMillis()
																						- begin);
		return response;
	}
}
