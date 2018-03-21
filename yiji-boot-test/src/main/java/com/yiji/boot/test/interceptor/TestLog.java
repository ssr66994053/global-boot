/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-04-28 19:22 创建
 */
package com.yiji.boot.test.interceptor;

/**
 * @author qiubo@yiji.com
 */

import java.lang.annotation.*;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TestLog {
	String desc();
}
