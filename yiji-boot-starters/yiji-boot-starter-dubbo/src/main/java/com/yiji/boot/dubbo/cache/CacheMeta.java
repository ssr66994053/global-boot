/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-18 16:59 创建
 */
package com.yiji.boot.dubbo.cache;

import com.yiji.boot.core.components.dubbo.DubboCache;
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
}
