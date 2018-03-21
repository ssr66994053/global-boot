/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-05-06 15:23 创建
 */
package com.yiji.boot.core.hera;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.env.EnumerableCompositePropertySource;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author qiubo@yiji.com
 */
public class HeraSpringApplicationRunListener implements SpringApplicationRunListener, PriorityOrdered {
	private SpringApplication application;
	private String[] args;
	public HeraSpringApplicationRunListener(SpringApplication application, String[] args) {
		this.application = application;
		this.args = args;
	}
	
	@Override
	public void started() {
		//提前启动hera,加快启动系统
		HeraStarter.start();
	}
	
	@Override
	public void environmentPrepared(ConfigurableEnvironment environment) {
		
	}
	
	@Override
	public void contextPrepared(ConfigurableApplicationContext context) {
		
	}
	
	@Override
	public void contextLoaded(ConfigurableApplicationContext context) {
		HeraStarter.handleResult();
		new PropertySourcesLog().log(context.getEnvironment());
	}
	
	@Override
	public void finished(ConfigurableApplicationContext context, Throwable exception) {
		
	}
	
	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}
	
	public static class PropertySourcesLog {
		private Logger logger = LoggerFactory.getLogger(PropertySourcesLog.class);
		
		/**
		 * 打印PropertySources
		 * @param environment 环境
		 */
		public void log(ConfigurableEnvironment environment) {
			StringBuilder infos = new StringBuilder();
			environment
				.getPropertySources()
				.forEach(env -> {
					if (env.getClass().getSimpleName().contains("ConfigurationPropertySources")) {
						//打印文件配置
					try {
						Field field = env.getClass().getDeclaredField("sources");
						field.setAccessible(true);
						List list = (List) ReflectionUtils.getField(field, env);
						list.forEach(s -> {
							if (s instanceof EnumerableCompositePropertySource) {
								EnumerableCompositePropertySource enumerableCompositePropertySource = (EnumerableCompositePropertySource) s;
								enumerableCompositePropertySource.getSource().forEach(
									a -> infos.append('\t').append(a.toString()).append("\n"));
							}
						});
					} catch (NoSuchFieldException e) {
						//do nothing
					}
				} else {
					infos.append('\t').append(env.toString()).append("\n");
				}
				
			}	);
			logger.info("应用PropertySources优先级:\n{}", infos.toString());
		}
	}
}
