/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-16 15:59 创建
 */
package com.yiji.boot.test.dubbo;

import com.yiji.boot.core.components.dubbo.DubboCache;
import com.yjf.common.lang.result.SingleValueResult;
import com.yjf.common.service.SingleValueOrder;

/**
 * @author qiubo@yiji.com
 */

public interface CacheableService {
	String CACHE_NAME="test";
	@DubboCache(cacheName = CACHE_NAME,key = "#p0.playload")
	SingleValueResult<String> echo(SingleValueOrder<String> order);
}
