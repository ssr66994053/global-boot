/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-16 17:51 创建
 *
 */
package com.global.boot.velocity;

import com.google.common.base.Charsets;
import com.global.boot.core.EnvironmentHolder;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.velocity.VelocityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.ui.velocity.SpringResourceLoader;
import org.springframework.ui.velocity.VelocityEngineFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;
import org.springframework.web.servlet.view.velocity.VelocityLayoutViewResolver;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

import javax.servlet.ServletContext;
import java.util.Properties;

/**
 * @author qiubo@yiji.com
 */
@Configuration
@ConditionalOnClass({ VelocityEngine.class, VelocityEngineFactory.class })
@ConditionalOnWebApplication
@EnableConfigurationProperties({ VelocityProperties.class, YijiVelocityProperties.class })
@ConditionalOnProperty(name = { "spring.velocity.enabled", "yiji.velocity.enabled" }, matchIfMissing = true)
public class VelocityAutoConfiguration {
	private static final String RESOURCE_LOADER_NAME = "yijiMacro";
	
	private static final String RESOURCE_LOADER_CLASS = "yijiMacro.resource.loader.class";
	@Autowired
	private Environment environment;
	@Autowired
	protected VelocityProperties properties;
	
	@Autowired
	private YijiVelocityProperties yijiVelocityProperties;
	
	@Bean
	public VelocityConfigurer velocityConfigurer() {
		YijiVelocityConfigurer configurer = new YijiVelocityConfigurer();
		configurer.setExposeSpringMacroHelpers(exposeSpringMacroHelpers());
		configurer.setResourceLoaderPath(this.properties.getResourceLoaderPath());
		//设置vm文件是否优先从文件系统加载，但是如果从文件系统加载不了，也不会从classpath加载，⊙﹏⊙b汗
		//这里需要优化加载行为,如果文件系统有，则从文件系统加载(文件系统加载可以hotload)，没有则从类路径加载
		//改为从classpath加载
		configurer.setPreferFileSystemAccess(false);
		configurer.setOverrideLogging(false);
		Properties velocityProperties = new Properties();
		velocityProperties.putAll(this.properties.getProperties());
		velocityProperties.put("input.encoding", Charsets.UTF_8.name());
		velocityProperties.put("output.encoding", Charsets.UTF_8.name());
		velocityProperties.put("parser.pool.size", 200);
		velocityProperties.put("velocimacro.library", yijiVelocityProperties.buildMacroLocs());
		velocityProperties.put("velocimacro.library.autoreload", false);
		velocityProperties.put(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, new VelocityLogChute());
		configurer.setVelocityProperties(velocityProperties);
		return configurer;
	}
	
	private static class YijiVelocityConfigurer extends VelocityConfigurer {
		private ServletContext servletContext;
		private boolean exposeSpringMacroHelpers;
		
		@Override
		public void setServletContext(ServletContext servletContext) {
			this.servletContext = servletContext;
		}
		
		public boolean isExposeSpringMacroHelpers() {
			return exposeSpringMacroHelpers;
		}
		
		public void setExposeSpringMacroHelpers(boolean exposeSpringMacroHelpers) {
			this.exposeSpringMacroHelpers = exposeSpringMacroHelpers;
		}
		
		/**
		 * disable spring macro if possible
		 */
		@Override
		protected void postProcessVelocityEngine(VelocityEngine velocityEngine) {
			if (isExposeSpringMacroHelpers()) {
				super.postProcessVelocityEngine(velocityEngine);
			} else {
				velocityEngine.setApplicationAttribute(ServletContext.class.getName(), this.servletContext);
				velocityEngine.setProperty(RESOURCE_LOADER_CLASS, ClasspathResourceLoader.class.getName());
				velocityEngine.addProperty(VelocityEngine.RESOURCE_LOADER, RESOURCE_LOADER_NAME);
			}
		}
		
		@Override
		protected void initSpringResourceLoader(VelocityEngine velocityEngine, String resourceLoaderPath) {
			super.initSpringResourceLoader(velocityEngine, resourceLoaderPath);
			velocityEngine.setProperty(SpringResourceLoader.SPRING_RESOURCE_LOADER_CLASS,
				YijiResourceLoader.class.getName());
			String velocityCache = EnvironmentHolder.get().getProperty("spring.velocity.cache");
			if (!StringUtils.hasText(velocityCache)) {
				velocityEngine.setProperty(SpringResourceLoader.SPRING_RESOURCE_LOADER_CACHE, "true");
			} else {
				velocityEngine.setProperty(SpringResourceLoader.SPRING_RESOURCE_LOADER_CACHE, velocityCache);
			}
			
		}
	}
	
	/**
	 * 注册VelocityViewResolver bean name为 {@link DispatcherServlet#VIEW_RESOLVER_BEAN_NAME}，spring mvc会使用此ViewResolver
	 * disable  {@link org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration.VelocityWebConfiguration#velocityViewResolver()}
	 * disable {@link org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter#viewResolver(org.springframework.beans.factory.BeanFactory)}
	 */
	@Bean(name = {DispatcherServlet.VIEW_RESOLVER_BEAN_NAME,"velocityViewResolver"})
	public VelocityViewResolver velocityViewResolver() {
		if (yijiVelocityProperties.isLayoutEnable()) {
			VelocityLayoutViewResolver resolver = new VelocityLayoutViewResolver();
			this.properties.applyToViewResolver(resolver);
			customVelocityViewResolver(resolver);
			resolver.setViewClass(YijiVelocityLayoutView.class);
			resolver.setLayoutUrl("layout/layout.vm");
			return resolver;
		} else {
			VelocityViewResolver resolver = new VelocityViewResolver();
			this.properties.applyToViewResolver(resolver);
			customVelocityViewResolver(resolver);
			resolver.setViewClass(YijiVelocityView.class);
			return resolver;
		}
	}
	
	private void customVelocityViewResolver(VelocityViewResolver resolver) {
		//鉴于分布式session性能考虑，不准许暴露session属性到model
		resolver.setExposeSessionAttributes(false);
		resolver.setExposeSpringMacroHelpers(exposeSpringMacroHelpers());
		resolver.setToolboxConfigLocation(yijiVelocityProperties.getToolboxConfigLocation());
		String velocityCache = environment.getProperty("spring.velocity.cache");
		if (!StringUtils.hasText(velocityCache)) {
			resolver.setCache(true);
		} else {
			resolver.setCache(Boolean.valueOf(velocityCache));
		}
	}
	
	/**
	 * <p>
	 * 是否暴露spring macro，默认关闭
	 * <p/>
	 * 在我们的场景，我们没有用到spring macro spring.vm 源代码路径 spring-webmvc org.springframework.web.servlet.view.velocity.spring.vm
	 */
	private boolean exposeSpringMacroHelpers() {
		String exposeSpringMacro = environment.getProperty("spring.velocity.expose-spring-macro-helpers");
		if (!StringUtils.hasLength(exposeSpringMacro)) {
			return false;
		}
		return Boolean.valueOf(exposeSpringMacro);
	}
}
