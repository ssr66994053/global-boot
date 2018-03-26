/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-16 17:52 创建
 *
 */
package com.global.boot.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.global.boot.core.Apps;
import com.global.boot.tomcat.TomcatProperties;
import com.global.boot.web.datacollector.DataCollectorController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcProperties;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author qiubo@yiji.com
 */
@Configuration
@EnableWebMvc
@ConditionalOnWebApplication
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class, WebMvcConfigurerAdapter.class })
@EnableConfigurationProperties({ WebMvcProperties.class, ResourceProperties.class, YijiWebProperties.class })
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class SpringMVCAutoConfiguration extends WebMvcConfigurerAdapter implements ApplicationContextAware,
																		InitializingBean {
	private static final Logger logger = LoggerFactory.getLogger(SpringMVCAutoConfiguration.class);
	
	private static final String[] SERVLET_RESOURCE_LOCATIONS = { "/" };
	
	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/",
																	"classpath:/resources/", "classpath:/static/",
																	"classpath:/public/" };
	
	private static final String[] RESOURCE_LOCATIONS;
	public static final String SIMPLE_URL_MAPPING_VIEW_CONTROLLER = "simpleUrlMappingViewController";
	
	static {
		RESOURCE_LOCATIONS = new String[CLASSPATH_RESOURCE_LOCATIONS.length + SERVLET_RESOURCE_LOCATIONS.length];
		System.arraycopy(SERVLET_RESOURCE_LOCATIONS, 0, RESOURCE_LOCATIONS, 0, SERVLET_RESOURCE_LOCATIONS.length);
		System.arraycopy(CLASSPATH_RESOURCE_LOCATIONS, 0, RESOURCE_LOCATIONS, SERVLET_RESOURCE_LOCATIONS.length,
			CLASSPATH_RESOURCE_LOCATIONS.length);
	}
	
	@Autowired
	private ResourceProperties resourceProperties = new ResourceProperties();
	
	@Autowired
	private TomcatProperties tomcatProperties;
	
	@Autowired
	private YijiWebProperties yijiWebProperties;
	
	private ApplicationContext applicationContext;

	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		//设置 welcome-file
		if (!Strings.isNullOrEmpty(yijiWebProperties.getWelcomeFile())) {
			String welcomeFile = yijiWebProperties.getWelcomeFile();
			if (!welcomeFile.startsWith("/")) {
				welcomeFile = "forward:/" + welcomeFile;
			} else {
				welcomeFile = "forward:" + welcomeFile;
			}
			registry.addViewController("/").setViewName(welcomeFile);
			registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		}
	}
	
	/**
	 * 自定义静态资源处理
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		if (!this.resourceProperties.isAddMappings()) {
			logger.debug("Default resource handling disabled");
			return;
		}
		
		Integer cachePeriod = this.resourceProperties.getCachePeriod();
		if (!registry.hasMappingForPattern("/webjars/**")) {
			registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/")
				.setCachePeriod(cachePeriod);
		}
		if (!registry.hasMappingForPattern("/**")) {
			registry.addResourceHandler("/**").addResourceLocations(RESOURCE_LOCATIONS).setCachePeriod(cachePeriod);
		}
	}
	
	/**
	 * 增加fastjson HttpMessageConverter
	 */
	@Bean
	@ConditionalOnClass(JSON.class)
	@ConditionalOnMissingBean
	public YijiFastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
		YijiFastJsonHttpMessageConverter converter = new YijiFastJsonHttpMessageConverter();
		converter.setSupportedMediaTypes(Lists.newArrayList(MediaType.APPLICATION_JSON));
		converter.setFeatures(SerializerFeature.WriteDateUseDateFormat,
			SerializerFeature.DisableCircularReferenceDetect);
		return converter;
	}
	
	/**
	 * 去掉Jackson converter
	 */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		Iterator<HttpMessageConverter<?>> iterator = converters.iterator();
		while (iterator.hasNext()) {
			HttpMessageConverter<?> converter = iterator.next();
			//remove MappingJackson2HttpMessageConverter
			if (converter.getClass().toString().endsWith("MappingJackson2HttpMessageConverter")) {
				iterator.remove();
			}
		}
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		Map<String, Object> map = applicationContext.getBeansWithAnnotation(SpringHandlerInterceptor.class);
		map.values().forEach(
			o -> {
				if (o instanceof HandlerInterceptor) {
					SpringHandlerInterceptor springHandlerInterceptor = o.getClass().getAnnotation(
						SpringHandlerInterceptor.class);
					InterceptorRegistration registration = registry.addInterceptor((HandlerInterceptor) o);
					if (springHandlerInterceptor.includePatterns() != null
						&& springHandlerInterceptor.includePatterns().length != 0) {
						registration.addPathPatterns(springHandlerInterceptor.includePatterns());
					}
					if (springHandlerInterceptor.excludePatterns() != null
						&& springHandlerInterceptor.excludePatterns().length != 0) {
						registration.excludePathPatterns(springHandlerInterceptor.excludePatterns());
					}
				}
			});
	}
	
	@Bean
	@ConditionalOnProperty(value = "yiji.web.globalExceptionHandlerEnable", matchIfMissing = true)
	public GlobalExceptionHandler globalExceptionHandler() {
		return new GlobalExceptionHandler();
	}
	
	/**
	 * 配置模板直接映射bean
	 */
	@Bean
	public SimpleUrlHandlerMapping directUrlHandlerMapping(YijiWebProperties yijiWebProperties) {
		SimpleUrlHandlerMapping directUrlHandlerMapping = new SimpleUrlHandlerMapping();
		directUrlHandlerMapping.setOrder(Integer.MAX_VALUE - 2);
		Map<String, Object> urlMap = new HashMap<>();
		for (String url : yijiWebProperties.buildMappingUrlList()) {
			urlMap.put(url, SIMPLE_URL_MAPPING_VIEW_CONTROLLER);
		}
		directUrlHandlerMapping.setUrlMap(urlMap);
		return directUrlHandlerMapping;
	}
	
	/**
	 * 配置模板直接映射controller
	 */
	@Bean(name = SIMPLE_URL_MAPPING_VIEW_CONTROLLER)
	public SimpleUrlMappingViewController simpleUrlMappingViewController(YijiWebProperties yijiWebProperties) {
		SimpleUrlMappingViewController simpleUrlMappingViewController = new SimpleUrlMappingViewController();
		Map<String, String> viewNameMap = yijiWebProperties.buildViewNameMap();
		if (!viewNameMap.isEmpty()) {
			logger.info("配置url直接映射模板:{}", viewNameMap);
		}
		simpleUrlMappingViewController.setViewNameMap(viewNameMap);
		return simpleUrlMappingViewController;
	}
	
	@Bean
	@ConditionalOnBean(HiddenHttpMethodFilter.class)
	public FilterRegistrationBean disableHiddenHttpMethodFilter(HiddenHttpMethodFilter filter,
																YijiWebProperties yijiWebProperties) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(yijiWebProperties.isHiddenHttpMethodFilterEnable());
		return registration;
	}
	
	@Bean
	@ConditionalOnBean(HttpPutFormContentFilter.class)
	public FilterRegistrationBean disableHttpPutFormContentFilter(HttpPutFormContentFilter filter,
																	YijiWebProperties yijiWebProperties) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(yijiWebProperties.isHttpPutFormContentFilterEnable());
		return registration;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, WebMvcAutoConfiguration> beansOfType = applicationContext
			.getBeansOfType(WebMvcAutoConfiguration.class);
		if (beansOfType.isEmpty()) {
			logger.error("yiji-boot spring mvc 没有正确加载WebMvcAutoConfiguration,原因可能有:");
			logger.error("1. JavaConfig中配置了@EnableWebMvc");
			logger.error("2. 引入了spring-mvc xml配置文件");
			Apps.shutdown();
		}
	}
	
	@Bean
	@ConditionalOnProperty(value = "yiji.dataCollector.enable")
	public DataCollectorController dataCollectorController() {
		return new DataCollectorController();
	}
	
	@Configuration
	@ConditionalOnProperty(value = "yiji.tomcat.jsp.enable")
	public static class JSPAutoConfiguration implements ApplicationContextAware {
		
		private ApplicationContext applicationContext;
		@Autowired
		private TomcatProperties tomcatProperties;
		
		@Bean
		public FilterRegistrationBean jspFileAccessFilter() {
			FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new Filter() {
				@Override
				public void init(FilterConfig filterConfig) throws ServletException {
					
				}
				
				@Override
				public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
																											throws IOException,
																											ServletException {
					if (response instanceof HttpServletResponse) {
						HttpServletResponse httpServletResponse = (HttpServletResponse) response;
						String uri = ((HttpServletRequest) request).getRequestURI();
						if (uri.contains(".jsp")) {
							httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
							response.getWriter().write("NOT FOUND");
						} else {
							chain.doFilter(request, response);
						}
					}
				}
				
				@Override
				public void destroy() {
					
				}
			});
			filterRegistrationBean.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC));
			return filterRegistrationBean;
		}
		
		/**
		 * 配置jsp ViewResolver
		 */
		@PostConstruct
		public void jspViewResolver() {
			try {
				InternalResourceViewResolver internalResourceViewResolver = applicationContext
					.getBean(InternalResourceViewResolver.class);
				internalResourceViewResolver.setPrefix(tomcatProperties.getJsp().getPrefix());
				internalResourceViewResolver.setSuffix(tomcatProperties.getJsp().getSuffix());
			} catch (BeansException e) {
				// do nothing
			}
			
		}
		
		@Override
		public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
			this.applicationContext = applicationContext;
		}
	}
	
}
