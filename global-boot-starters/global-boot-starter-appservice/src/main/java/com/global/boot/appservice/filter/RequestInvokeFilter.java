/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-09-22 11:19 创建
 */
package com.global.boot.appservice.filter;

import com.global.boot.filterchain.Filter;
import com.global.boot.filterchain.FilterChain;
import com.yjf.common.lang.exception.Exceptions;
import org.springframework.core.Ordered;

import java.lang.reflect.InvocationTargetException;

/**
 * @author qiubo@yiji.com
 */
public class RequestInvokeFilter implements Filter<AppServiceContext> {
	@Override
	public void doFilter(AppServiceContext context, FilterChain<AppServiceContext> filterChain) {
		
		try {
			Object result = context.getMethodInvocation().getMethod()
				.invoke(context.getTarget(), context.getMethodInvocation().getArguments());
			context.setResult(result);
		} catch (IllegalAccessException e) {
			throw Exceptions.newRuntimeException(e);
		} catch (InvocationTargetException e) {
			context.setTargetThrowable(e.getTargetException());
		}
		
	}
	
	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}
}
