/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-18 16:16 创建
 */
package com.global.boot.dubbo.cache;

/**
 * @author qiubo@yiji.com
 */
public interface KeyGenerator {
	Object key(CacheMeta cacheMeta,Object[] args);
}
