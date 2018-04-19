/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-10-27 12:26 创建
 *
 */
package com.global.boot.actuator;

import com.google.common.collect.Lists;
import com.global.boot.actuator.acl.ActuatorACLFilter;
import com.global.boot.actuator.controller.ActuatorIndexController;
import com.global.boot.actuator.controller.LogOperateServlet;
import com.global.boot.actuator.endpoint.IOEndpoint;
import com.global.boot.actuator.endpoint.JarEndpoint;
import com.global.boot.actuator.health.check.DeadlockedThreadsHealthIndicator;
import com.global.boot.actuator.health.check.MemoryStatusHealthIndicator;
import com.global.boot.actuator.health.check.SystemLoadHealthIndicator;
import com.global.boot.actuator.health.check.ThreadsCountHealthIndicator;

//import com.global.boot.actuator.health.guard.HealthGuard;
//import com.global.boot.actuator.health.guard.HealthGuardStartingListener;


import com.global.boot.actuator.metrics.GCMetrics;
import com.global.boot.actuator.metrics.file.MetricsFileWriter;
import com.global.boot.actuator.metrics.opentsdb.OpenTsdbProperties;
//import com.global.boot.actuator.metrics.opentsdb.YijiTsdbGaugeWriter;
import com.global.boot.core.Apps;
//import com.global.boot.core.hera.HeraStarter;
//import com.yiji.framework.watcher.http.adaptor.web.WatcherServlet;
//import com.yiji.framework.watcher.spring.SpringApplicationContextHolder;
import com.global.common.id.CodeGenerator;
import com.global.common.portrait.model.Endpoint;
import com.global.common.portrait.model.HTTPEndpoint;
import com.global.common.portrait.model.IOResource;
import com.global.common.portrait.model.TCPEndpoint;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.boot.actuate.endpoint.MetricsEndpointMetricReader;
import org.springframework.boot.actuate.endpoint.TomcatPublicMetrics;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.Filter;
import java.util.List;

/**
 * @author qiubo@yiji.com
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(OpenTsdbProperties.class)
public class ActuatorAutoConfiguration {
	@Bean
	public DeadlockedThreadsHealthIndicator deadlockedThreadsHealthIndicator() {
		return new DeadlockedThreadsHealthIndicator();
	}
	
	//	@Bean
	public MemoryStatusHealthIndicator memoryStatusHealthIndicator() {
		return new MemoryStatusHealthIndicator();
	}
	
	@Bean
	public ThreadsCountHealthIndicator threadsCountHealthIndicator() {
		return new ThreadsCountHealthIndicator();
	}
	
	@Bean
	public SystemLoadHealthIndicator systemLoadHealthIndicator() {
		return new SystemLoadHealthIndicator();
	}
	
	@Bean
	@ConditionalOnClass(RequestMapping.class)
	public ActuatorIndexController actuatorIndexController() {
		return new ActuatorIndexController();
	}
	
	@Bean
	public FilterRegistrationBean actuatorACLFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(new ActuatorACLFilter());
		filterRegistrationBean.addUrlPatterns("/mgt/*");
		return filterRegistrationBean;
	}
	
//	@ConditionalOnWebApplication
//	@Bean
//	public ServletRegistrationBean actuatorWatcherServlet() {
//		WatcherServlet watcherServlet = new WatcherServlet(Apps.getAppName());
//		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(watcherServlet, "/mgt/watcher/*");
//		servletRegistrationBean.setName("watcherServlet");
//		return servletRegistrationBean;
//	}
//	
//	@ConditionalOnMissingBean(SpringApplicationContextHolder.class)
//	@Bean
//	public SpringApplicationContextHolder registerActuatorWatcherSpringApplicationContextHolder() {
//		return new SpringApplicationContextHolder();
//	}
	
	/**
	 * 禁用applicationContextIdFilter,在我们的场景没有什么卵用
	 * @param beanFactory
	 * @return
	 */
	@Bean
	@ConditionalOnWebApplication
	@ConditionalOnBean(name = "applicationContextIdFilter")
	public FilterRegistrationBean disableApplicationContextIdFilter(BeanFactory beanFactory) {
		Object object = beanFactory.getBean("applicationContextIdFilter");
		if (object instanceof Filter) {
			FilterRegistrationBean registration = new FilterRegistrationBean((Filter) object);
			registration.setEnabled(false);
			return registration;
		}
		return null;
	}
	
//	@Bean(name = "healthGuard")
//	public HealthGuard healthGuard() {
//		return new HealthGuard();
//	}
//	
//	@Bean
//	public ApplicationListener healthGuardStartingListener() {
//		return new HealthGuardStartingListener();
//	}
//	
//	@ExportMetricWriter
//	@Bean(name = "tsdbGaugeWriter")
//	public YijiTsdbGaugeWriter tsdbGaugeWriter(OpenTsdbProperties properties) {
//		System.setProperty("spring.metrics.export.triggers.tsdbGaugeWriter.excludes", properties.getValidExcludes());
//		System.setProperty("spring.metrics.export.triggers.tsdbGaugeWriter.delayMillis", "15000");
//		return new YijiTsdbGaugeWriter(properties.getValidateUrls());
//	}
	
	@Bean
	public MetricsEndpointMetricReader metricsEndpointMetricReader(	MetricsEndpoint endpoint,
																	TomcatPublicMetrics tomcatPublicMetrics) {
		endpoint.unregisterPublicMetrics(tomcatPublicMetrics);
		return new MetricsEndpointMetricReader(endpoint);
	}
	
	@Bean
	public MetricsFileWriter metricsFileWriter() {
		return new MetricsFileWriter();
	}
	
	@Bean
	public JarEndpoint jarEndpoint() {
		return new JarEndpoint();
	}
	
	@Bean
	public ActuatorAccessChecker actuatorAccessChecker() {
		return new ActuatorAccessChecker();
	}
	
	@Bean
	public ThreadPoolChecker threadPoolChecker() {
		return new ThreadPoolChecker();
	}
	
	@Bean
	public IOEndpoint ioEndpoint() {
		return new IOEndpoint();
	}
	
//	@Bean
//	public IOResource<Endpoint> heraResouces() {
//		return () -> {
//			List<Endpoint> endpoints = Lists.newArrayList();
//			if (HeraStarter.isHeraEnable()) {
//				TCPEndpoint tcpEndpoint = new TCPEndpoint();
//				tcpEndpoint.parseIpAndPort(System.getProperty(Apps.HERA_ZK_URL));
//				tcpEndpoint.setName("zk");
//				tcpEndpoint.setDesc("hera");
//				endpoints.add(tcpEndpoint);
//			}
//			HTTPEndpoint httpEndpoint = new HTTPEndpoint();
//			httpEndpoint.setUrl(CodeGenerator.getHeraRegisterUrl());
//			httpEndpoint.setName("register");
//			httpEndpoint.setDesc("hera");
//			endpoints.add(httpEndpoint);
//			return endpoints;
//		};
//	}
	
	@Bean
	public GCMetrics gcMetrics() {
		return new GCMetrics();
	}
	
	@Bean
	public ServletRegistrationBean logOperateRegistrationBean() {
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
		servletRegistrationBean.setName("logOperateServlet");
		LogOperateServlet logOperateServlet = new LogOperateServlet();
		servletRegistrationBean.setServlet(logOperateServlet);
		servletRegistrationBean.setUrlMappings(Lists.newArrayList("/mgt/logOperate/*"));
		servletRegistrationBean.setLoadOnStartup(1);
		return servletRegistrationBean;
	}
}
