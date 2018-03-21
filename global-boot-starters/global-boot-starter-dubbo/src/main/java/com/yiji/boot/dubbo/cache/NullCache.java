/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-19 19:16 创建
 */
package com.yiji.boot.dubbo.cache;

/**
 * @author qiubo@yiji.com
 */
public class NullCache extends RedisCache {
	public static NullCache INSTANCE = new NullCache();
}
