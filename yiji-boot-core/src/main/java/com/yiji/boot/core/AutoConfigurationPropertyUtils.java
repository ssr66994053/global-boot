/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * Agrael 2015年9月8日 上午8:48:33 创建
 */
package com.yiji.boot.core;

import com.yjf.common.lang.RunException;
import com.yjf.common.util.ReflectionUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

/**
 * 自动配置属性工具。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
public abstract class AutoConfigurationPropertyUtils {
	
	/**
	 * 安全的复制一个非空的属性值。 当且仅当<code>source</code>与<code>target</code>不为 <code>null</code>且<code>property</code>
	 * 存在对应的属性，并且复制的属性不为 <code>null</code>时才会复制。
	 * @param source 复制属性值的源对象。
	 * @param target 属性复制到的目标对象。
	 * @param property 复制属性的名称。
	 * @return 如果复制成功返回true，由于复制条件不满足返回false。
	 * @throws RunException 如果复制发生异常。
	 */
	public static boolean safeCopyNotNullProperty(Object source, Object target, String property) {
		Assert.notNull(property, "{property}不能为'null'。");
		return safeCopyNotNullProperty0(source, target, property, property);
	}
	
	/**
	 * 安全的复制一个非空的属性值。 当且仅当<code>source</code>与<code>target</code>不为 <code>null</code>且<code>sourceProperty</code>与
	 * <code>targetProperty</code>都存在对应的属性，并且复制的属性不为<code>null</code>时才会复制。
	 * @param source 复制属性值的源对象。
	 * @param target 属性复制到的目标对象。
	 * @param sourceProperty 属性源对象需要复制的属性名称。
	 * @param targetProperty 目标对象目标复制属性的名称。
	 * @return 如果复制成功返回true，由于复制条件不满足返回false。
	 * @throws RunException 如果复制发生异常。
	 */
	public static boolean safeCopyNotNullProperty(Object source, Object target, String sourceProperty,
													String targetProperty) {
		Assert.notNull(sourceProperty, "{sourceProperty}不能为'null'。");
		Assert.notNull(targetProperty, "{targetProperty}不能为'null'。");
		return safeCopyNotNullProperty0(source, target, sourceProperty, targetProperty);
	}
	
	private static boolean safeCopyNotNullProperty0(Object source, Object target, String sourceProperty,
													String targetProperty) {
		if (source == null || target == null) {
			return false;
		}
		Method getterMethod;
		try {
			getterMethod = ReflectionUtils.getGetterMethod(source, sourceProperty);
		} catch (NoSuchMethodException e) {
			return false;
		}
		Method setterMethod;
		try {
			setterMethod = ReflectionUtils.getSetterMethod(target, targetProperty);
		} catch (NoSuchMethodException e) {
			return false;
		}
		Object property = ReflectionUtils.executeMethod(source, getterMethod);
		if (property == null) {
			return false;
		}
		ReflectionUtils.executeMethod(target, setterMethod, property);
		return true;
	}
	
}
