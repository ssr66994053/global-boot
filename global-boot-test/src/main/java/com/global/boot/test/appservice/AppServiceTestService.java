/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-10-10 20:21 创建
 */
package com.yiji.boot.test.appservice;

import com.yiji.boot.appservice.AppService;
import com.yjf.common.lang.result.SingleValueResult;
import com.yjf.common.service.SingleValueOrder;

/**
 * @author qiubo@yiji.com
 */
@AppService
public class AppServiceTestService {
	
	public SingleValueResult<AppDto> test(SingleValueOrder<AppDto> appRequest) {
		//do what you like
		return SingleValueResult.from(appRequest.getPlayload());
	}
	
	@AppService.ValidationGroup(AppDto.Test1.class)
	public SingleValueResult<AppDto> test1(SingleValueOrder<AppDto> appRequest) {
		//do what you like
		return SingleValueResult.from(appRequest.getPlayload());
	}
	
	@AppService.ValidationGroup(AppDto.Test2.class)
	public SingleValueResult<AppDto> test2(SingleValueOrder<AppDto> appRequest) {
		//do what you like
		return SingleValueResult.from(appRequest.getPlayload());
	}
	
	@AppService.ValidationGroup(value = AppDto.Test2.class, checkDefaultGroup = false)
	public SingleValueResult<AppDto> test3(SingleValueOrder<AppDto> appRequest) {
		//do what you like
		return SingleValueResult.from(appRequest.getPlayload());
	}
}
