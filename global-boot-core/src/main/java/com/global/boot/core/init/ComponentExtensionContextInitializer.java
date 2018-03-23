/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-09-10 10:21 创建
 *
 */
package com.global.boot.core.init;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.List;

/**
 * @author qiubo@yiji.com
 */
@Order(value = Ordered.LOWEST_PRECEDENCE)
public class ComponentExtensionContextInitializer implements ApplicationContextInitializer {
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(applicationContext.getEnvironment(),
			"spring.autoconfigure.");
		String[] exclude = resolver.getProperty("exclude", String[].class);
		if (exclude == null) {
			exclude = new String[0];
		}
		List<String> excludes = Lists.newArrayList(exclude);
		SpringFactoriesLoader
			.loadFactories(com.global.boot.core.init.ComponentInitializer.class, applicationContext.getClassLoader())
			.forEach(componentInitializer -> {
				componentInitializer.initialize(applicationContext);
				List<String> list = componentInitializer.excludeAutoconfigClassNames();
				if (list != null && !list.isEmpty()) {
					excludes.addAll(list);
				}
			});
		if (!excludes.isEmpty()) {
			System.setProperty("spring.autoconfigure.exclude", Joiner.on(',').join(excludes));
		}
	}
}
