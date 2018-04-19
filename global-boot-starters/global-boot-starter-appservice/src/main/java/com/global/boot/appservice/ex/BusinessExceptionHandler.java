/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * lixiang@yiji.com 2014-12-03 17:47 创建
 *
 */
package com.global.boot.appservice.ex;

import com.global.common.lang.exception.BusinessException;
import com.global.common.lang.result.StandardResultInfo;
import com.global.common.lang.result.Status;
import com.global.common.util.StringUtils;

/**
 * {@link BusinessException} 处理器。
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 */
public class BusinessExceptionHandler implements ExceptionHandler<BusinessException> {
	
	public void handle(ExceptionContext<?> context, BusinessException e) {
		StandardResultInfo res = context.getResponse();
		if (StringUtils.isNotBlank(e.getDescription())) {
			res.setDescription(e.getDescription());
		} else {
			res.setDescription(e.getMessage());
		}
		res.setStatus(Status.FAIL);
		res.setCode(e.getCode());
	}
}
