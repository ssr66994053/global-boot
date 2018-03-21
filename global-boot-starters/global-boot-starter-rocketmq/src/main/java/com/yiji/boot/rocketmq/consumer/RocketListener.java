/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-02-01 13:53 创建
 *
 */
package com.yiji.boot.rocketmq.consumer;

import java.lang.annotation.*;

/**
 * @author yanglie@yiji.com
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RocketListener {
	/**
	 * 设置监听的topic
	 */
	String topic() default "";
	
	MessageFilter messageFilter();
	
	/**
	 * 是否是监听的有序消息, 默认为false
	 */
	boolean ordered() default false;
	
	/**
	 * 是否开启日志，打印每个接收到的消息，默认为true
	 */
	boolean enableLog() default true;
}
