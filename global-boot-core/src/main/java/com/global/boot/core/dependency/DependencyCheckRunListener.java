/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-05-17 19:13 创建
 */
package com.global.boot.core.dependency;

import com.global.common.dependency.DependencyChecker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.ClassUtils;

import java.util.List;

/**
 * 我们应用有很多非标准化的依赖，比如依赖一个外部特殊的服务，比如依赖jdk提供某方面的能力。我们需要在系统启动阶段就能检查这样的能力， 如果系统不提供这样的能力，应该提前发现问题，让系统启动报错。
 * @author qiubo@yiji.com
 */
public class DependencyCheckRunListener implements SpringApplicationRunListener {
	private SpringApplication application;
	private String[] args;
	
	public DependencyCheckRunListener(SpringApplication application, String[] args) {
		this.application = application;
		this.args = args;
	}
	
	@Override
	public void started() {
		
	}
	
	@Override
	public void environmentPrepared(ConfigurableEnvironment configurableEnvironment) {
		
	}
	
	@Override
	public void contextPrepared(ConfigurableApplicationContext configurableApplicationContext) {
	}
	
	@Override
	public void contextLoaded(ConfigurableApplicationContext configurableApplicationContext) {
		List<DependencyChecker> dependencyCheckers = SpringFactoriesLoader.loadFactories(DependencyChecker.class,
			ClassUtils.getDefaultClassLoader());
		dependencyCheckers.stream().forEach(dependencyChecker -> {
			dependencyChecker.check(configurableApplicationContext.getEnvironment());
		});
	}
	
	@Override
	public void finished(ConfigurableApplicationContext configurableApplicationContext, Throwable throwable) {
		
	}
}
