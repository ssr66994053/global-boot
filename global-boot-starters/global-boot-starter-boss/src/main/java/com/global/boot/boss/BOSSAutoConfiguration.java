/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 *
 * 修订记录:
 * zhouxi@yiji.com 2015-09-15 15:41 创建
 *
 */

package com.global.boot.boss;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.config.spring.AnnotationBean;
import com.yiji.boot.core.Apps;
import com.global.boot.dubbo.DubboAutoConfiguration;
import com.global.boot.session.YedisHttpSessionConfiguration;
import com.global.boot.xss.XSSProperties;
import com.global.boot.yedis.YedisAutoConfiguration;
import com.global.common.concurrent.MonitoredThreadPool;
import com.global.common.lang.beans.Copier;
import com.global.common.web.CrossScriptingFilter;
import com.yjf.marmot.MarmotFilterProxyBean;
import com.yjf.marmot.shiro.MarmotRealm;
import com.yjf.marmot.shiro.UserInfoRoleToken;
import com.yjf.marmot.shiro.cluster.cache.MarmotCacheManager;
import com.yjf.marmot.shiro.cluster.support.redis.RedisCacheManagerHelper;
import com.yjf.marmot.shiro.filter.DefaultPremissionFilter;
import com.yjf.marmot.user.MarmotUser;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.web.http.SessionRepositoryFilter;

import javax.servlet.Filter;
import java.util.HashMap;

/**
 * @author zhouxi@yiji.com
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(value = "yiji.boss.enable", matchIfMissing = true)
@EnableConfigurationProperties({ BOSSProperties.class })
@AutoConfigureAfter({ YedisHttpSessionConfiguration.class, YedisAutoConfiguration.class, DubboAutoConfiguration.class })
@ComponentScan(basePackageClasses = { BossInterceptor.class })
public class BOSSAutoConfiguration implements InitializingBean {
	
	@Value("${yiji.marmot.serverAddr}")
	private String marmotServerAddress;
	
	@Value("${yiji.marmot.ticket.validationAddr}")
	private String marmotServerTicketValidationAddress;
	
	@Value("${yiji.marmot.mgtServerAddr}")
	private String manageServerAddress;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private BOSSProperties bossProperties;
	
	@Autowired
	protected XSSProperties xssProperties;
	
	@Autowired
	private AnnotationBean annotationBean;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Bean
	public RabbitTemplate bossLogMQTemplate(@Qualifier("rabbitConnectionFactory") CachingConnectionFactory rabbitConnectionFactory,
											MessageConverter kyroMessageConverter) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(rabbitConnectionFactory);
		rabbitTemplate.setMessageConverter(kyroMessageConverter);
		rabbitTemplate.setQueue(bossProperties.getLogMqName());
		rabbitTemplate.setRoutingKey(bossProperties.getLogMqName());
		return rabbitTemplate;
	}
	
	@Bean
	public FilterRegistrationBean bossXssFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(newBossXssFilter());
		filterRegistrationBean.addUrlPatterns("/boss/*");
		filterRegistrationBean.setName("bossXssFilter");
		return filterRegistrationBean;
	}
	
	private CrossScriptingFilter newBossXssFilter() {
		CrossScriptingFilter crossScriptingFilter = new CrossScriptingFilter();
		XSSProperties bossXssProperties = Copier.copy(xssProperties, XSSProperties.class);
		bossXssProperties.setEscapeHtml(true);
		crossScriptingFilter.setXssConfig(bossXssProperties);
		return crossScriptingFilter;
	}
	
	@Bean
	public MonitoredThreadPool bossLogThreadPool() {
		MonitoredThreadPool monitoredThreadPool = new MonitoredThreadPool();
		monitoredThreadPool.setCorePoolSize(1);
		monitoredThreadPool.setMaxPoolSize(2);
		monitoredThreadPool.setQueueCapacity(100);
		monitoredThreadPool.setThreadNamePrefix("boss-log-");
		return monitoredThreadPool;
	}
	
	@Bean
	@ConditionalOnMissingBean(LifecycleBeanPostProcessor.class)
	public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}
	
	@Bean
	public MarmotCacheManager marmotCacheManager(@Qualifier("redisTemplate") RedisTemplate redisTemplate) {
		RedisCacheManagerHelper helper = new RedisCacheManagerHelper();
		helper.setRedisTemplate(redisTemplate);
		MarmotCacheManager cacheManager = new MarmotCacheManager();
		cacheManager.setMarmotCacheManagerHelper(helper);
		return cacheManager;
	}
	
	@Bean
	public static MarmotRealm marmotRealm() {
		MarmotRealm marmotRealm = new MarmotRealm();
		marmotRealm.setAuthenticationTokenClass(UserInfoRoleToken.class);
		return marmotRealm;
	}
	
	@Bean
	public AuthorizationAttributeSourceAdvisor marmotAuthorizationAttributeSourceAdvisor(	WebSecurityManager marmotSecurityManager) {
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(marmotSecurityManager);
		return advisor;
	}
	
	@Bean
	public WebSecurityManager marmotSecurityManager(Realm marmotRealm,
													@Qualifier("marmotCacheManager") CacheManager marmotCacheManager) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(marmotRealm);
		securityManager.setCacheManager(marmotCacheManager);
		return securityManager;
	}
	
	@Bean
	public Object marmotMethodInvokingFactoryBean(WebSecurityManager marmotSecurityManager) throws Exception {
		MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
		methodInvokingFactoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
		methodInvokingFactoryBean.setArguments(new Object[] { marmotSecurityManager });
		methodInvokingFactoryBean.afterPropertiesSet();
		return methodInvokingFactoryBean.getObject();
	}
	
	@Bean
	public MarmotFilterProxyBean marmotFilterProxyBean(@Qualifier("redisTemplate") RedisTemplate redisTemplate,
														WebSecurityManager marmotSecurityManager) {
		MarmotFilterProxyBean marmotFilterProxyBean = new MarmotFilterProxyBean();
		marmotFilterProxyBean.setEnableUaa(true);
		marmotFilterProxyBean.setServerName(bossProperties.getServerAddress());
		marmotFilterProxyBean.setMgtServerUrl(manageServerAddress);
		marmotFilterProxyBean.setMgtServer(bossProperties.isManageServer());
		marmotFilterProxyBean.setEnableProxy(false);
		marmotFilterProxyBean.setRedisTemplate(redisTemplate);
		marmotFilterProxyBean.setRedirect(true);
		marmotFilterProxyBean.setSecurityManager(marmotSecurityManager);
		marmotFilterProxyBean.setFilters(new HashMap<String, Filter>());
		marmotFilterProxyBean.setMarmotServerUrlPrefix(marmotServerTicketValidationAddress);
		if (env.acceptsProfiles("online", "koala", "pre")) {
			marmotFilterProxyBean.setMarmotServerLoginUrl(marmotServerAddress + "/cas/login");
		} else {
			marmotFilterProxyBean.setMarmotServerLoginUrl(marmotServerAddress + "/login");
		}
		if ((bossProperties.isEnablePermission() || env.acceptsProfiles("online", "koala", "pre"))
			&& !bossProperties.isManageServer()) {
			marmotFilterProxyBean.getFilters().put("permissionFilter", new DefaultPremissionFilter());
			marmotFilterProxyBean.setFilterChainDefinitions(BOSSProperties.FILTER_CHAIN_DEF);
		} else {
			marmotFilterProxyBean.setFilterChainDefinitions(BOSSProperties.FILTER_CHAIN_DEF_NO_PERMISSION);
		}
		if (Apps.isDevMode()) {
			marmotFilterProxyBean.setRedirect(false);
		}
		return marmotFilterProxyBean;
		
	}
	
	@Order(SessionRepositoryFilter.DEFAULT_ORDER + 1)
	@Bean(name = "marmotFilter")
	Filter createMarmotFilter(MarmotFilterProxyBean marmotFilterProxyBean) {
		return (Filter) marmotFilterProxyBean.getObject();
	}
	
	@Bean
	public FilterRegistrationBean someFilterRegistration(Filter marmotFilter) {
		
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(marmotFilter);
		registration.addUrlPatterns("/boss/" + Apps.getAppName() + "/*");
		registration.setName("marmotFilter");
		return registration;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		MarmotUser marmotUser = new BossPermissionAdapter();
		annotationBean.setPackage(annotationBean.getPackage() + Constants.COMMA_SEPARATOR
									+ BossPermissionAdapter.class.getPackage().getName());
		marmotUser = (MarmotUser) annotationBean.postProcessBeforeInitialization(marmotUser, marmotUser.getClass()
			.getName());
		
		ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext)
			.getBeanFactory();
		beanFactory.registerSingleton(marmotUser.getClass().getCanonicalName(), marmotUser);
		MarmotRealm marmotRealm = applicationContext.getBean(MarmotRealm.class);
		marmotRealm.setMarmotUser(marmotUser);
	}
}
