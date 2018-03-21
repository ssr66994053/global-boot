/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-09-22 10:47 创建
 */
package com.yiji.boot.appservice.filter;

import com.yiji.boot.filterchain.Context;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author qiubo@yiji.com
 */
public class AppServiceContext extends Context {
	private MethodInvocation methodInvocation;
	private Object target;
	private Object result;
	private Throwable targetThrowable;
	
	public Object getResult() {
		return result;
	}
	
	public void setResult(Object result) {
		this.result = result;
	}
	
	public MethodInvocation getMethodInvocation() {
		return methodInvocation;
	}
	
	public void setMethodInvocation(MethodInvocation methodInvocation) {
		this.methodInvocation = methodInvocation;
	}
	
	public Object getTarget() {
		return target;
	}
	
	public void setTarget(Object target) {
		this.target = target;
	}
	
	public Throwable getTargetThrowable() {
		return targetThrowable;
	}
	
	public void setTargetThrowable(Throwable targetThrowable) {
		this.targetThrowable = targetThrowable;
	}
	
	public String getLoggerMethodName() {
		String simpleName = this.target.getClass().getSimpleName();
		int idx = simpleName.indexOf("$$");
		if (idx != -1) {
			simpleName = simpleName.substring(0, idx);
		}
		return simpleName + "#" + this.methodInvocation.getMethod().getName();
	}
}
