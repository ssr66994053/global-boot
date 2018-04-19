/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2014-12-03 01:53 创建
 *
 */
package com.global.boot.appservice.ex;

import com.global.common.lang.enums.CommonErrorCode;
import com.global.common.lang.result.StandardResultInfo;
import com.global.common.lang.result.Status;

/**
 * @author qzhanbo@yiji.com
 */
public class ThrowableExceptionHandler implements ExceptionHandler<Throwable> {
	
	@Override
	public void handle(ExceptionContext<?> context, Throwable throwable) {
		StandardResultInfo res = context.getResponse();
		res.setDescription(throwable.getMessage());
		res.setStatus(Status.FAIL);
		res.setCode(CommonErrorCode.UNKNOWN_ERROR.getCode());
	}
}
