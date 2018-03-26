/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-02-01 13:57 创建
 *
 */
package com.global.boot.rocketmq.consumer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yanglie@yiji.com
 */
@Target({ ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageFilter {
	/**
	 * 必填：消息的发送系统
	 */
	String[] fromSystem() default {};
	
	/**
	 * 选填：消息的接收系统，如果不设，将会默认为当前系统，对应@YijiBootApplication的sysName
	 */
	String[] toSystem() default {};
	
	/**
	 * 必填：指定消息的event类型
	 */
	String[] event() default {};
	
	/**
	 * 选填：消息所在的环境，如果不设，默认为当前环境，对应spring.active.profile
	 */
	String[] env() default {};
}
