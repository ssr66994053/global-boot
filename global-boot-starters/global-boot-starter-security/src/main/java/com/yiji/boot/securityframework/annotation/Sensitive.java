/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * LiXiang 2016年4月25日 下午10:55:42 创建
 */
package com.yiji.boot.securityframework.annotation;

import java.lang.annotation.*;

/**
 * 被该注解所标注的方法表明为一个敏感的方法，需要进行安全处理。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Sensitive {
	
}
