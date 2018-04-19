/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-18 16:59 创建
 */
package com.global.boot.dubbo.cache;

import com.global.boot.core.components.dubbo.DubboCache;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author qiubo@yiji.com
 */
@Data
public class CacheMeta {
	private Method method;
	private Class<?>[] parameterTypes;
	private DubboCache dubboCache;
	private Class targetClass;
	private String namespace;

	public String getMethodFullName(){
		return this.targetClass.getSimpleName()+'#'+method.getName();
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public DubboCache getDubboCache() {
		return dubboCache;
	}

	public void setDubboCache(DubboCache dubboCache) {
		this.dubboCache = dubboCache;
	}

	public Class getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(Class targetClass) {
		this.targetClass = targetClass;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	
}
