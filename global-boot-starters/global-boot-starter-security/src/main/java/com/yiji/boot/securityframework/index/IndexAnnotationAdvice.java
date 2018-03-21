/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * LiXiang 2016年4月25日 下午6:40:52 创建
 */
package com.yiji.boot.securityframework.index;

import com.yiji.common.security.annotation.support.IndexProcessor;
import com.yjf.common.util.StringUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ProxyMethodInvocation;

import java.util.List;

/**
 * 索引相关的注解的Advice。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
public class IndexAnnotationAdvice extends IndexProcessor implements MethodInterceptor {
	
	protected String getSecurityUser(String securityUser, String securityUserRef, ProcessContext processContext) {
		boolean debugEnabled = this.logger.isDebugEnabled();
		if (StringUtils.hasText(securityUser)) {
			if (debugEnabled) {
				this.logger.debug("直接使用安全用户'{}'。", securityUser);
			}
			return securityUser;
		}
		securityUser = StringUtils.toString(processContext.getParameter(securityUserRef));
		if (StringUtils.hasText(securityUser)) {
			if (debugEnabled) {
				this.logger.debug("使用从参数'{}'中得到的安全用户'{}'。", securityUserRef, securityUser);
			}
			return securityUser;
		}
		securityUser = this.defaultSecurityUser;
		if (debugEnabled) {
			this.logger.debug("使用默认的安全用户'{}'。", securityUser);
		}
		return securityUser;
	}
	
	public Object invoke(MethodInvocation invocation) throws Throwable {
		ProcessContext processContext = new ProcessContext(invocation.getMethod(), invocation.getArguments());
		process(processContext);
		if (invocation instanceof ProxyMethodInvocation) {
			List<Object> methodParameters = processContext.getMethodParameters();
			((ProxyMethodInvocation) invocation).setArguments(methodParameters.toArray(new Object[methodParameters
				.size()]));
		}
		Object result = invocation.proceed();
		processContext = new ProcessContext(invocation.getMethod(), result);
		process(processContext);
		return processContext.getMethodResult();
	}
	
}
