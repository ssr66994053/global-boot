/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-06 15:41 创建
 *
 */
package com.global.boot.tomcat;

import com.global.boot.core.AppConfigException;
import com.global.boot.core.Apps;
//import com.global.framework.hera.client.exception.HeraException;
//import com.global.framework.hera.client.listener.Event;
//import com.global.framework.hera.client.listener.ValueTrigger;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.valves.AccessLogValve;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.JspServlet;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.io.File;
import java.nio.charset.Charset;

/**
 * @author qiubo@yiji.com
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(Tomcat.class)
@EnableConfigurationProperties({ TomcatProperties.class })
public class TomcatAutoConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(TomcatAutoConfiguration.class);
	
	private AbstractProtocol abstractProtocol;
	
	@Autowired
	private TomcatProperties tomcatProperties;
	
	@Bean(name = "yijiEmbeddedServletContainerCustomizer")
	public EmbeddedServletContainerCustomizer embeddedServletContainerCustomizer() {
		
		return container -> {
			//1. disable jsp if possible
			if (!tomcatProperties.getJsp().isEnable()) {
				JspServlet jspServlet = new JspServlet();
				jspServlet.setRegistered(false);
				container.setJspServlet(jspServlet);
			}
			
			//2. 定制tomcat
			if (container instanceof TomcatEmbeddedServletContainerFactory) {
				TomcatEmbeddedServletContainerFactory factory = (TomcatEmbeddedServletContainerFactory) container;
				factory.setUriEncoding(Charset.forName(tomcatProperties.getUriEncoding()));
				setTomcatWorkDir(factory);
				//2.1 设置最大线程数为400
				factory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
					ProtocolHandler handler = connector.getProtocolHandler();
					if (handler instanceof AbstractProtocol) {
						@SuppressWarnings("rawtypes")
						AbstractProtocol protocol = (AbstractProtocol) handler;
						protocol.setMaxThreads(tomcatProperties.getMaxThreads());
						protocol.setMinSpareThreads(tomcatProperties.getMinSpareThreads());
						abstractProtocol = protocol;
					}
					connector.setAttribute("acceptCount", "100");
				});
				//2.2 设置访问日志目录和日志格式
				if (tomcatProperties.isAccessLogEnable()) {
					if (factory.getValves().stream().anyMatch((valve) -> valve instanceof AccessLogValve)) {
						throw new AppConfigException("AccessLogValve已经配置，请不要启用默认spring-boot AccessLogValve配置");
					}
					AccessLogValve valve = new AccessLogValve();
					//参数含义参考AbstractAccessLogValve 注释
					valve.setPattern(TomcatProperties.HTTP_ACCESS_LOG_FORMAT);
					valve.setSuffix(".log");
					//读取真实ip，参考：org.apache.catalina.valves.AbstractAccessLogValve.HostElement
					valve.setRequestAttributesEnabled(true);
					valve.setDirectory(Apps.getLogPath());
					factory.addContextValves(valve);
				}
			}
		};
	}
	
	private void setTomcatWorkDir(TomcatEmbeddedServletContainerFactory factory) {
		//设置tomcat base dir
		File file = new File(Apps.getAppDataPath() + "/tomcat-" + Apps.getHttpPort());
		file.mkdirs();
		factory.setBaseDirectory(file);
		file.deleteOnExit();
		//设置tomcat doc base dir
		File docbase = new File(Apps.getAppDataPath() + "/tomcat-docbase-" + Apps.getHttpPort());
		docbase.mkdirs();
		factory.setDocumentRoot(docbase);
		docbase.deleteOnExit();
		logger.info("设置tomcat baseDir={},docbase={}", file, docbase);
	}
	
	@Bean
	@DependsOn("yijiEmbeddedServletContainerCustomizer")
	public TomcatMetrics tomcatMetrics() {
		return new TomcatMetrics(abstractProtocol);
	}
	
	@Bean
	@DependsOn("yijiEmbeddedServletContainerCustomizer")
	public TomcatHealthIndicator tomcatHealthIndicator() {
		return new TomcatHealthIndicator(abstractProtocol, tomcatProperties);
	}
	
//	@Override
//	public void onChange(Event event) throws HeraException {
//		event.ifPresent("yiji.tomcat.maxThreads", value -> {
//			int maxThreads = Integer.valueOf(value);
//			abstractProtocol.setMaxThreads(maxThreads);
//			tomcatProperties.setMaxThreads(maxThreads);
//			logger.info("hera修改tomcat配置maxThreads:{}", maxThreads);
//		});
//	}
}
