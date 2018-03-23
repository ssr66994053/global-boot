/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-10 19:17 创建
 *
 */
package com.global.boot.core;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.annotation.*;

/**
 * @author qiubo@yiji.com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootApplication
public @interface YijiBootApplication {
	/**
	 * 系统名称
	 */
	String sysName();
	
	/**
	 * 是否启用配置管理系统，启用配置管理系统后，hera会加入到Environment propertysource中
	 */
	boolean heraEnable() default true;
	
	/**
	 * http端口[-1=关闭,0=随机]
	 */
	int httpPort() default 0;
	
}
