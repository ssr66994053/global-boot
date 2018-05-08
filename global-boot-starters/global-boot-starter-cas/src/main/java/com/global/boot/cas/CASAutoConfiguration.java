/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 *
 * 修订记录:
 * shuijing@yiji.com 2016-07-07 15:41 创建
 *
 */
package com.global.boot.cas;

import com.google.common.collect.Maps;
import com.yiji.authcenter.cache.AuthcenterCacheManager;
import com.yiji.authcenter.factory.CasShiroFilterFactoryBean;
import com.yiji.authcenter.filter.CasFilter;
import com.yiji.authcenter.filter.CasLogoutFilter;
import com.yiji.authcenter.filter.hander.DealLoginUrlHandlerInterceptor;
import com.yiji.authcenter.mgt.CasDefaultSecurityManager;
import com.yiji.authcenter.realm.YijiCasRealm;
import com.yiji.authcenter.redis.RedisCacheManagerHelper;
import com.yiji.authcenter.redis.RedisManager;
import com.global.boot.core.Apps;
import com.global.boot.session.YedisHttpSessionConfiguration;
import com.global.boot.yedis.YedisAutoConfiguration;
import com.yjf.common.env.Env;
import org.apache.commons.collections.MapUtils;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.cas.CasSubjectFactory;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.session.mgt.ServletContainerSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.data.redis.YijiRedisOperationsSessionRepository;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * @author shuijing@yiji.com
 */

@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(value = "yiji.cas.enable", matchIfMissing = true)
@EnableConfigurationProperties({ CASProperties.class })
@AutoConfigureAfter({ YedisHttpSessionConfiguration.class, YedisAutoConfiguration.class })
public class CASAutoConfiguration {

	@Autowired
	Environment environment;
	@Bean
	public CasShiroFilterFactoryBean casShiroFilterFactoryBean(	@Qualifier("casSecurityManager") WebSecurityManager casSecurityManager,
																@Qualifier("casFilter") CasFilter casFilter,
																@Qualifier("casRedisManager") RedisManager casRedisManager,
																@Qualifier("casLogoutFilter") Filter casLogoutFilter,
																@Qualifier("sessionRepository") YijiRedisOperationsSessionRepository sessionRepository,
																CASProperties casProperties) {
		
		CasShiroFilterFactoryBean casShiroFilterFactoryBean = new CasShiroFilterFactoryBean();
		casShiroFilterFactoryBean.setLoginUrl(casProperties.getCasServer() + "/authentication?TARGET="
												+ casProperties.getClientServerName() + "/shiro-cas&loginUrl="
												+ casProperties.getLoginUrl());
		casShiroFilterFactoryBean.setSecurityManager(casSecurityManager);
		casShiroFilterFactoryBean.setSuccessUrl(casProperties.getSuccessUrl());
		casShiroFilterFactoryBean.setUnauthorizedUrl(casProperties.getLoginUrl());
		Map<String, Filter> filters = Maps.newHashMap();
		filters.put("casFilter", casFilter);
		filters.put("casLogoutFilter", casLogoutFilter);
		casShiroFilterFactoryBean.setFilters(filters);
		casShiroFilterFactoryBean.setFilterChainDefinitions(getFilterChainDefinitions(casProperties));
		casShiroFilterFactoryBean.setRedisManager(casRedisManager);
		
		casShiroFilterFactoryBean.setLogoutRedirectUrl(casProperties.getLoginUrl() + "/");
		casShiroFilterFactoryBean.setSessionRepository(sessionRepository);
		
		return casShiroFilterFactoryBean;
	}
	
	@Order(SessionRepositoryFilter.DEFAULT_ORDER + 1)
	@Bean(name = "authCenterFilter")
	public Filter getCasShiroFilter(@Qualifier("casShiroFilterFactoryBean") ShiroFilterFactoryBean casShiroFilterFactoryBean,
									@Qualifier("casRealm") CasRealm casRealm) throws Exception {
		((DefaultWebSecurityManager) casShiroFilterFactoryBean.getSecurityManager()).setRealm(casRealm);
		return (Filter) casShiroFilterFactoryBean.getObject();
	}
	
	@Bean
	public FilterRegistrationBean casFilterRegistrationBean(@Qualifier("authCenterFilter") Filter authCenterFilter,
															CASProperties casProperties) {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(authCenterFilter);
		filterRegistrationBean.setName("authCenterFilter");
		filterRegistrationBean.addUrlPatterns(casProperties.getUrlPattern());
		filterRegistrationBean.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC));
		return filterRegistrationBean;
	}
	
	@Bean
	public FilterRegistrationBean casLogoutFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(casLogoutFilter());
		registration.addUrlPatterns("/*");
		registration.addInitParameter("excludedUrl", "/mgt/*");
		registration.setName("casLogoutFilter");
		registration.setOrder(1);
		registration.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC));
		return registration;
	}
	
	/**
	 * 安全管理器
	 */
	@Bean(name = "casSecurityManager")
	public WebSecurityManager getDefaultWebSecurityManager(	SubjectFactory casSubjectFactory,
															@Qualifier("authcenterCacheManager") CacheManager authcenterCacheManager) {
		CasDefaultSecurityManager defaultWebSecurityManager = new CasDefaultSecurityManager();
		defaultWebSecurityManager.setSubjectFactory(casSubjectFactory);
		defaultWebSecurityManager.setSessionManager(sessionManager());
		defaultWebSecurityManager.setCacheManager(authcenterCacheManager);
		return defaultWebSecurityManager;
	}
	
	@Bean(name = "authcenterCacheManager")
	public AuthcenterCacheManager marmotCacheManager(@Qualifier("redisTemplate") RedisTemplate redisTemplate) {
		RedisCacheManagerHelper helper = new RedisCacheManagerHelper();
		helper.setRedisTemplate(redisTemplate);
		AuthcenterCacheManager cacheManager = new AuthcenterCacheManager();
		cacheManager.setAuthcenterCacheManagerHelper(helper);
		return cacheManager;
	}
	
	@Bean(name = "casRedisManager")
	public RedisManager getCasRedisManager(@Qualifier("redisTemplate") RedisTemplate redisTemplate,
											CASProperties casProperties) {
		RedisManager redisManager = new com.yiji.authcenter.redis.RedisManager();
		redisManager.setRedisTemplate(redisTemplate);
		redisManager.setExpire(casProperties.getRedisManagerExpireTime());
		return redisManager;
	}
	
	@Bean(name = "casRealm")
	public CasRealm casRealm(CASProperties casProperties) {
		CasRealm casRealm = new YijiCasRealm();
		//casRealm.setDefaultPermissions(casProperties.getDefaultPermissions());
		//casRealm.setDefaultRoles(casProperties.getDefaultRoles());
		casRealm.setCasService(casProperties.getClientServerName() + "/shiro-cas");
		//线上预发布serverUrlPrefix有端口,需要单独配置
		if(Env.isPre() || Env.isOnline() || Env.isSnetx()){
			casRealm.setCasServerUrlPrefix(casProperties.getServerUrlPrefix() + "/");
		}else {
			casRealm.setCasServerUrlPrefix(casProperties.getCasServer() + "/");
		}
		casRealm.setValidationProtocol("SAML");
		return casRealm;
	}
	
	@Bean
	public Object casMethodInvokingFactoryBean(@Qualifier("casSecurityManager") WebSecurityManager casSecurityManager,
												@Qualifier("casRealm") CasRealm casRealm) throws Exception {
		((DefaultWebSecurityManager) casSecurityManager).setRealm(casRealm);
		MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
		methodInvokingFactoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
		methodInvokingFactoryBean.setArguments(new Object[] { casSecurityManager });
		methodInvokingFactoryBean.afterPropertiesSet();
		return methodInvokingFactoryBean.getObject();
	}
	
	/**
	 * 于开启 Shiro Spring AOP 权限注解的支持
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(	WebSecurityManager casSecurityManager,
																					@Qualifier("casRealm") CasRealm casRealm) {
		((DefaultWebSecurityManager) casSecurityManager).setRealm(casRealm);
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(casSecurityManager);
		return authorizationAttributeSourceAdvisor;
	}
	
	/**
	 * Shiro生命周期处理器
	 */
	@Bean
	@ConditionalOnMissingBean(LifecycleBeanPostProcessor.class)
	public static LifecycleBeanPostProcessor casLifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}
	
	@Bean
	public DealLoginUrlHandlerInterceptor dealLoginUrlHandlerInterceptor(CASProperties casProperties) {
		DealLoginUrlHandlerInterceptor dealInterceptor=new DealLoginUrlHandlerInterceptor();
		dealInterceptor.setCasServer(casProperties.getCasServer());
		dealInterceptor.setClientServerName(casProperties.getClientServerName());
		dealInterceptor.setLoginUrl(casProperties.getLoginUrl());
		dealInterceptor.setLoginErrorUrl(environment.getProperty("yiji.cas.loginErrorUrl"));
		return dealInterceptor;
	}
	
	@Bean(name = "casFilter")
	public CasFilter getCasFilter(CASProperties casProperties) {
		CasFilter casFilter = new CasFilter();
		casFilter.setFailureUrl(casProperties.getLoginUrl());
		return casFilter;
	}
	
	@Bean
	public WebMvcConfigurer casCheckConfigurerAdapter(CASProperties casProperties) {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				registry.addInterceptor(dealLoginUrlHandlerInterceptor(casProperties)).addPathPatterns("/**");
			}
		};
	}
	
	@Bean(name = "casLogoutFilter")
	public Filter casLogoutFilter() {
		return new CasLogoutFilter();
	}
	
	@Bean
	public SessionManager sessionManager() {
		return new ServletContainerSessionManager();
	}
	
	@Bean
	public SubjectFactory casSubjectFactory() {
		return new CasSubjectFactory();
	}
	
	private String getFilterChainDefinitions(CASProperties casProperties) {
		List<Map<String, String>> urls = casProperties.getExcludeUrls();
		if (CollectionUtils.isEmpty(urls)) {
			return CASProperties.FILTER_CHAIN_DEF + excludeLoginUrl(casProperties)
					+ (isExcludedAuthcApp(casProperties) ? "" : CASProperties.FILTER_AUTHC_DEF);
		}
		StringBuilder sb = new StringBuilder();
		for (Map<String, String> url : urls) {
			if (MapUtils.isNotEmpty(url)) {
				for (Map.Entry<String, String> entry : url.entrySet()) {
					sb.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
				}
			}
		}
		return CASProperties.FILTER_CHAIN_DEF + excludeLoginUrl(casProperties) + sb.toString()
				+ (isExcludedAuthcApp(casProperties) ? "" : CASProperties.FILTER_AUTHC_DEF);
	}
	
	/**
	 * 判断是完全不需要shiro授权的系统
	 */
	private boolean isExcludedAuthcApp(CASProperties casProperties) {
		for (String app : casProperties.getExcludedAuthcApp()) {
			if (Apps.getAppName().equals(app))
				return true;
		}
		return false;
	}
	
	private String excludeLoginUrl(CASProperties casProperties) {
		String loginUrl = casProperties.getLoginUrl();
		if (loginUrl != null) {
			if (loginUrl.indexOf("//") != -1) {
				String loginStr = loginUrl.substring(loginUrl.indexOf("//") + 2, loginUrl.length());
				if (loginStr.indexOf("/") != -1) {
					return loginStr.substring(loginStr.indexOf("/"), loginStr.length()) + "= anon\n";
				}
			}
		}
		return "";
	}
}
