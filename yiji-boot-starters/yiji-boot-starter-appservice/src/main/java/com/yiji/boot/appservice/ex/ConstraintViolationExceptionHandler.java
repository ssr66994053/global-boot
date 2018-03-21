/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-09-28 11:16 创建
 */
package com.yiji.boot.appservice.ex;

import com.yjf.common.lang.enums.CommonErrorCode;
import com.yjf.common.lang.result.StandardResultInfo;
import com.yjf.common.lang.result.Status;
import com.yjf.common.service.OrderCheckException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * @author qiubo@yiji.com
 */
public class ConstraintViolationExceptionHandler implements ExceptionHandler<ConstraintViolationException> {
	@Override
	public void handle(ExceptionContext<?> context, ConstraintViolationException e) {
		OrderCheckException exception = new OrderCheckException();
		for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
			exception.addError(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
		}
		StandardResultInfo res = context.getResponse();
		res.setDescription(exception.getMessage());
		res.setStatus(Status.FAIL);
		res.setCode(CommonErrorCode.INVALID_ARGUMENTS.code());
	}
}
