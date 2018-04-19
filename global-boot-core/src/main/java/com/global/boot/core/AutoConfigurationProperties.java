/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * Agrael 2015年9月7日 下午4:59:37 创建
 */
package com.global.boot.core;

import com.global.common.lang.RunException;

/**
 * 表示自动配置属性。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
public interface AutoConfigurationProperties {
	
	/**
	 * 合并属性到路径。
	 * @param path 需要合并的路径。
	 * @param property 需要合并到路径的属性。
	 * @return 合并后的路径。
	 */
	static String mergePropertyPath(String path, String property) {
		return path + "." + property;
	}
	
	/**
	 * 合并属性到路径。
	 * @param property 需要合并到路径的属性。
	 * @return 合并后的路径。
	 */
	default String mergePropertyPath(String property) {
		return mergePropertyPath(getPath(), property);
	}
	
	/**
	 * 安全的复制一个非空的属性值。 当且仅当<code>target</code>不为 <code>null</code>且 <code>property</code>存在对应的属性，并且复制的属性不为
	 * <code>null</code>时才会复制。
	 * @param target 属性复制到的目标对象。
	 * @param property 复制属性的名称。
	 * @return 如果复制成功返回true，由于复制条件不满足返回false。
	 * @throws RunException 如果复制发生异常。
	 * @see AutoConfigurationPropertyUtils#safeCopyNotNullProperty(Object, Object, String)
	 */
	default boolean copyNotNullProperty(Object target, String property) {
		return AutoConfigurationPropertyUtils.safeCopyNotNullProperty(this, target, property);
	}
	
	/**
	 * 安全的复制一个非空的属性值。 当且仅当<code>target</code>不为 <code>null</code>且 <code>sourceProperty</code>与
	 * <code>targetProperty</code> 都存在对应的属性，并且复制的属性不为<code>null</code>时才会复制。
	 * @param target 属性复制到的目标对象。
	 * @param sourceProperty 属性源对象需要复制的属性名称。
	 * @param targetProperty 目标对象目标复制属性的名称。
	 * @return 如果复制成功返回true，由于复制条件不满足返回false。
	 * @throws RunException 如果复制发生异常。
	 * @see AutoConfigurationPropertyUtils#safeCopyNotNullProperty(Object, Object, String, String)
	 */
	default boolean copyNotNullProperty(Object target, String sourceProperty, String targetProperty) {
		return AutoConfigurationPropertyUtils.safeCopyNotNullProperty(this, target, sourceProperty, targetProperty);
	}
	
	/**
	 * 得到表达式取得该对象的路径。
	 * @return 表达式取得该对象的路径。
	 */
	String getPath();
	
	default void setPath(String path) {
	}
	
}
