/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2014-12-03 00:41 创建
 *
 */
package com.global.boot.appservice.ex;

import com.global.common.lang.enums.CommonErrorCode;
import com.global.common.lang.result.StandardResultInfo;
import com.global.common.lang.result.Status;
import com.global.common.service.OrderCheckException;

/**
 * @author qzhanbo@yiji.com
 */
public class OrderCheckExceptionHandler implements ExceptionHandler<OrderCheckException> {
	
	@Override
	public void handle(ExceptionContext<?> context, OrderCheckException ex) {
		StandardResultInfo res = context.getResponse();
		res.setDescription(ex.getMessage());
		res.setStatus(Status.FAIL);
		res.setCode(CommonErrorCode.INVALID_ARGUMENTS.code());
	}
}
