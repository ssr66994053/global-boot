/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved.
 */

/*
 * 修订记录：
 * 麦子（lvchen@yiji.com）  2016-04-25 创建
 */

package com.global.boot.boss.log.annotations;

import java.lang.annotation.*;

/**
 * {@code BossOperation} 使用这个注解记录boss操作，该注解只在controller层有效 starter会负责将操作信息发送到日志记录的MQ中，并由主boss系统存储下来。 用在方法上可以表示用户执行了这个方法
 *
 * @author 麦子（lvchen@yiji.com）
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BossOperation {
	
	/**
	 * 对这个参数，或者方法的描述，例如，商户开户。
	 * @return
	 */
	String description() default "";
	
	/**
	 * 是否忽略参数,设置为true时不收集参数信息,默认收集
	 * @return
	 */
	boolean ignoreParams() default false;
	
	/**
	 * 需要忽略的参数列表，部分参数为敏感参数时可以通过此忽略,支持ant路径
	 * @see org.springframework.util.AntPathMatcher
	 * @return
	 */
	String[] ignoreParameterList() default "";
	
	/**
	 * 需要掩码的参数列表，部分参数为敏感参数时可以通过掩码,支持ant路径
	 * @see org.springframework.util.AntPathMatcher
	 * @return
	 */
	String[] maskParameterList() default "";
	
	/**
	 * 忽略当前方法
	 * @return
	 */
	boolean ignore() default false;
	
}
