/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-09-22 10:56 创建
 */
package com.global.boot.appservice.filter;

import com.global.boot.appservice.ex.ExceptionContext;
import com.global.boot.appservice.ex.ExceptionHandlers;
import com.global.boot.filterchain.Filter;
import com.global.boot.filterchain.FilterChain;
import com.global.common.lang.enums.CommonErrorCode;
import com.global.common.lang.exception.SystemException;
import com.global.common.lang.result.StandardResultInfo;
import com.global.common.lang.result.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

import java.lang.reflect.InvocationTargetException;

/**
 * @author qiubo@yiji.com
 */
public class ExceptionHandleFilter implements Filter<AppServiceContext> {
	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandleFilter.class);
	
	@Autowired
	private ExceptionHandlers exceptionHandlers;
	
	@Override
	public void doFilter(AppServiceContext context, FilterChain<AppServiceContext> filterChain) {
		try {
			filterChain.doFilter(context);
			Throwable targetThrowable = context.getTargetThrowable();
			if (targetThrowable != null) {
				handleThrowable(context, targetThrowable);
			}
		} catch (Throwable e) {
			handleThrowable(context, e);
		}
	}
	
	private void handleThrowable(AppServiceContext context, Throwable e) {
		if (e instanceof InvocationTargetException) {
			e = ((InvocationTargetException) e).getTargetException();
		}
		logger.error("处理异常:", e);
		Class<?> returnType = context.getMethodInvocation().getMethod().getReturnType();
		if (returnType != null && StandardResultInfo.class.isAssignableFrom(returnType)) {
			Object result = instantiate(returnType);
			StandardResultInfo newResult = (StandardResultInfo) result;
			ExceptionContext<?> exceptionContext = new ExceptionContext<>(newResult, context.getMethodInvocation()
				.getArguments());
			if (!exceptionHandlers.handle(exceptionContext, e)) {
				newResult.setDescription(e.getMessage());
				newResult.setStatus(Status.FAIL);
				newResult.setCode(CommonErrorCode.UNKNOWN_ERROR.code());
			}
			context.setResult(newResult);
		}
	}
	
	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 1;
	}
	
	private <T> T instantiate(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new SystemException(clazz.getName() + "不能初始化");
		}
	}
	
}
