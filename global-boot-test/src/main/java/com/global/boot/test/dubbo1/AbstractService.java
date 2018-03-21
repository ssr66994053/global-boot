/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-01-09 12:29 创建
 */
package com.yiji.boot.test.dubbo1;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yjf.customer.service.api.UserService;

/**
 * @author qiubo@yiji.com
 */
public abstract class AbstractService {
	@Reference(version = "1.5")
	protected UserService userService;
}
