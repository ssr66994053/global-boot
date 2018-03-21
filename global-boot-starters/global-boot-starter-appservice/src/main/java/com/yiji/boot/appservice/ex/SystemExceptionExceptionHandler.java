/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2014-12-03 01:56 创建
 *
 */
package com.yiji.boot.appservice.ex;

import com.yjf.common.lang.enums.CommonErrorCode;
import com.yjf.common.lang.exception.SystemException;
import com.yjf.common.lang.result.StandardResultInfo;
import com.yjf.common.lang.result.Status;

/**
 * @author qzhanbo@yiji.com
 */
public class SystemExceptionExceptionHandler implements ExceptionHandler<SystemException> {
	
	@Override
	public void handle(ExceptionContext<?> context, SystemException e) {
		StandardResultInfo res = context.getResponse();
		res.setDescription(e.getMessage());
		res.setStatus(Status.FAIL);
		res.setCode(CommonErrorCode.SYSTEM_ERROR.getCode());
	}
}
