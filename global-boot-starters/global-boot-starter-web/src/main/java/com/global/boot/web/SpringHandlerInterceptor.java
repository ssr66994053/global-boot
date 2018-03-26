/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-09-16 16:30 创建
 *
 */
package com.global.boot.web;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.annotation.*;

/**
 * spring mvc HandlerInterceptor注解，用于向spring 容器注入 {@link HandlerInterceptor}
 * <p>
 * {@link HandlerInterceptor} 请使用此注解，yiji-boot会扫描并加入到spring mvc中
 * @author qiubo@yiji.com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface SpringHandlerInterceptor {
	/**
	 * URL patterns to which the registered interceptor should apply to.
	 */
	String[] includePatterns() default {};
	
	/**
	 * URL patterns to which the registered interceptor should not apply to
	 */
	String[] excludePatterns() default {};
}
