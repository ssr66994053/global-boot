/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-09-10 10:56 创建
 *
 */
package com.global.boot.session;

import com.google.common.collect.Lists;
import com.global.boot.core.init.ComponentInitializer;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;

import java.util.List;

/**
 * @author qiubo@yiji.com
 */
public class SessionComponentInitializer implements ComponentInitializer {
	@Override
	public List<String> excludeAutoconfigClassNames() {
		return Lists.newArrayList(SessionAutoConfiguration.class.getName());
	}
}
