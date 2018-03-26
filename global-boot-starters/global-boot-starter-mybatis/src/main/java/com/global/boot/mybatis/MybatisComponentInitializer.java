/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-12 09:59 创建
 */
package com.global.boot.mybatis;

import com.google.common.collect.Lists;
import com.global.boot.core.init.ComponentInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

/**
 * @author qiubo@yiji.com
 */
public class MybatisComponentInitializer implements ComponentInitializer {
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		setPropertyIfMissing("mapper.mappers", "com.yiji.boot.mybatis.BaseMapper");
	}
	
	@Override
	public List<String> excludeAutoconfigClassNames() {
		return Lists.newArrayList("com.github.pagehelper.autoconfigure.MapperAutoConfiguration",
			"com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration");
	}
}
