/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-09-10 10:28 创建
 *
 */
package com.global.boot.core.init;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 用于组件排除spring boot默认加载的autoconfig配置
 * @author qiubo@yiji.com
 */
public interface AutoConfigExcluder {
	/**
	 * 返回需要排除的autoconfig class 类名
	 * @return 列表
	 */
	default List<String> excludeAutoconfigClassNames() {
		return Lists.newArrayList();
	}
}
