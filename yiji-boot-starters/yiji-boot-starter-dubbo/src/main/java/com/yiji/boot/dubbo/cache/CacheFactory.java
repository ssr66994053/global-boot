/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-18 16:24 创建
 */
package com.yiji.boot.dubbo.cache;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;

/**
 * @author qiubo@yiji.com
 */
public interface CacheFactory {
	RedisCache getCache(Invoker<?> invoker, Invocation invocation);
}
