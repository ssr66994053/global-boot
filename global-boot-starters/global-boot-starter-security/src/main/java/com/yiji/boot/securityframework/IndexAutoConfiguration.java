/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved.
 */

/*
 * 修订记录：
 * Agrael 2016年4月25日 下午7:07:46 创建
 */
package com.yiji.boot.securityframework;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.config.spring.AnnotationBean;
import com.yiji.boot.core.AutoConfigurationProperties;
import com.yiji.boot.core.AutoConfigurationPropertyUtils;
import com.yiji.boot.core.metrics.GenericObjectPoolMetrics;
import com.yiji.boot.securityframework.WebSecurityProperties.ProtectProperties;
import com.yiji.boot.securityframework.index.IndexAnnotationAdvice;
import com.yiji.boot.securityframework.index.domain.DomainIndexManager;
import com.yiji.boot.securityframework.index.ri.RiIndexManagerFactory;
import com.yiji.common.security.SecurityManager;
import com.yiji.common.security.annotation.NeedIndex;
import com.yiji.common.security.annotation.ReverseIndex;
import com.yiji.common.security.bean.DepositBox;
import com.yiji.common.security.bean.JavaBeanDepositBox;
import com.yiji.common.security.index.IndexManager;
import com.yiji.common.security.referenceimplements.SecurityConfig;
import com.yiji.framework.yedis.support.YedisCacheManager;
import com.yiji.framework.yedis.support.YedisConnectionFactory;
import com.yiji.framework.yedis.support.YedisSerializer;
import com.yiji.framework.yedis.support.YedisStringKeySerializer;
import com.yjf.common.concurrent.MonitoredThreadPool;
import com.yjf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.aspectj.autoproxy.AspectJAwareAdvisorAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import redis.clients.jedis.JedisPoolConfig;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 安全框架索引功能自动配置。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
@Configuration
@EnableConfigurationProperties({ SecurityProperties.class, IndexProperties.class })
@ConditionalOnProperty(value = { SecurityProperties.PATH + ".enable", IndexProperties.PATH + ".enable" },
		matchIfMissing = true)
public class IndexAutoConfiguration implements ApplicationContextAware {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexAutoConfiguration.class);
	
	private static final String JEDIS_CACHE_JMX_OBJECTNAME = "org.apache.commons.pool2:type=GenericObjectPool,name=redis.cache";
	
	private static final String JEDIS_CACHE_JMX_PREFIX = "redis.cache";
	
	private static final String CACHE_NAME_SPACE = "cache_securitydata:";
	
	private ApplicationContext applicationContext;
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private SecurityProperties securityProperties;
	
	@Autowired
	private IndexProperties indexProperties;
	
	@Autowired
	private AnnotationBean annotationBean;
	
	private Map<String, SecurityConfig> securityConfigs;
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	/**
	 * 在<code>domain</code>环境下创建一个名为<code>yijiBoot_SF_I_SecurityManager</code>的 {@link SecurityManager} 到容器。
	 * @return 创建的实例。
	 */
	@Bean(name = IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "IndexManager")
	@DependsOn(IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "RedisTemplate")
	@ConditionalOnProperty(name = IndexProperties.PATH + ".indexManagerEnv", havingValue = "domain",
			matchIfMissing = true)
	@ConditionalOnMissingBean(name = IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "IndexManager")
	public IndexManager createDomainIndexManager(	@Qualifier(IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX
																+ "RedisTemplate") RedisTemplate<String, Map<String, String>> redisTemplate) {
		this.annotationBean.setPackage(this.annotationBean.getPackage() + Constants.COMMA_SEPARATOR
										+ DomainIndexManager.class.getPackage().getName());
		DomainIndexManager indexManager = new DomainIndexManager(redisTemplate,indexProperties);
		indexManager.setIndexCacheTaskExecutor(indexCacheTaskExecutor());
		indexManager = (DomainIndexManager) this.annotationBean.postProcessBeforeInitialization(indexManager, indexManager
			.getClass().getName());
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("[domain]环境，系统不存在名为[{}]的Bean，创建类型为'{}'的Bean到容器。", IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX
																			+ "IndexManager", indexManager.getClass()
				.getName());
		}
		return indexManager;
	}

	public ThreadPoolTaskExecutor indexCacheTaskExecutor() {
		MonitoredThreadPool taskExecutor = new MonitoredThreadPool();
		taskExecutor.setCorePoolSize(5);
		taskExecutor.setKeepAliveSeconds(300);
		taskExecutor.setMaxPoolSize(100);
		taskExecutor.setQueueCapacity(500);
		taskExecutor.setAwaitTerminationSeconds(60);
		taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
		taskExecutor.setDaemon(false);
		taskExecutor.setThreadNamePrefix("IndexCache-");
		taskExecutor.initialize();
		return taskExecutor;
	}

	/**
	 * 在<code>ri</code>环境下创建一个名为<code>yijiBoot_SF_I_SecurityConfigs</code>的 安全配置印射 到容器。
	 * @return 创建的实例。
	 */
	@Bean(name = IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "SecurityConfigs")
	@ConditionalOnProperty(name = IndexProperties.PATH + ".indexManagerEnv", havingValue = "ri", matchIfMissing = true)
	@ConditionalOnMissingBean(name = IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "SecurityConfigs")
	public Map<String, SecurityConfig> createRiSecurityConfigs() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("[ri]环境，系统不存在名为[{}]的Bean，创建类型为'{}'的Bean到容器。", IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX
																		+ "SecurityConfigs",
				ConcurrentHashMap.class.getName());
		}
		this.securityConfigs = new ConcurrentHashMap<String, SecurityConfig>();
		return this.securityConfigs;
	}
	
	/**
	 * 在<code>ri</code>环境下创建一个名为<code>yijiBoot_SF_I_SecurityManager</code>的 {@link SecurityManager} 到容器。
	 * @return 创建的实例。
	 */
	@Bean(name = IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "IndexManager")
	@DependsOn(IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "SecurityConfigs")
	@ConditionalOnProperty(name = IndexProperties.PATH + ".indexManagerEnv", havingValue = "ri", matchIfMissing = true)
	@ConditionalOnMissingBean(name = IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "IndexManager")
	public IndexManager createRiIndexManager() {
		IndexManager indexManager = new RiIndexManagerFactory().createIndexManager(this.securityConfigs);
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("[ri]环境，系统不存在名为[{}]的Bean，创建类型为'{}'的Bean到容器。", IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX
																		+ "IndexManager", indexManager.getClass()
				.getName());
		}
		return indexManager;
	}
	
	/**
	 * 创建一个名为<code>yijiBoot_SF_I_DepositBox</code>的 {@link DepositBox} 到容器。
	 * @param indexManager 容器中需要的 {@link IndexManager} 实例。
	 * @return 创建到容器的 {@link DepositBox} 的实例。
	 */
	@Bean(name = IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "DepositBox")
	@DependsOn(IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "IndexManager")
	@ConditionalOnProperty(value = IndexProperties.PATH + ".enable", matchIfMissing = true)
	@ConditionalOnMissingBean(name = IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "DepositBox")
	public DepositBox createDepositBox(	@Qualifier(IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "IndexManager") IndexManager indexManager) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(
				"系统不存在[{}]的Bean，创建名称为'{}'类型为'{}'的Bean到容器，["
						+ AutoConfigurationProperties.mergePropertyPath(IndexProperties.PATH, "enable") + "]为'{}'。",
				DepositBox.class.getName(), IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "DepositBox",
				JavaBeanDepositBox.class.getName(), this.indexProperties.isEnable());
		}
		JavaBeanDepositBox javaBeanDepositBox = new JavaBeanDepositBox();
		javaBeanDepositBox.setIgnoreNoIndex(this.indexProperties.isReverseIndexeIgnoreNoIndex());
		if (indexManager != null) {
			javaBeanDepositBox.setIndexManager(indexManager);
		} else if (StringUtils.isBlank(this.indexProperties.getIndexManagerRefName())) {
			javaBeanDepositBox.setIndexManager(BeanUtils.getBean(this.applicationContext, IndexManager.class,
				this.indexProperties, "indexManagerRefName"));
		} else {
			javaBeanDepositBox.setIndexManager(BeanUtils.getBean(this.applicationContext,
				this.indexProperties.getIndexManagerRefName(), IndexManager.class, this.indexProperties,
				"indexManagerRefName"));
		}
		return javaBeanDepositBox;
	}
	
	/**
	 * 创建一个 DefaultAdvisorAutoProxyCreator 到容器。
	 * @return 创建到容器的 DefaultAdvisorAutoProxyCreator 的实例。
	 */
	@Bean
	@ConditionalOnProperty(value = { ProtectProperties.PATH + ".enable" }, matchIfMissing = true)
	@ConditionalOnMissingBean(AspectJAwareAdvisorAutoProxyCreator.class)
	public AnnotationAwareAspectJAutoProxyCreator createDefaultAdvisorAutoProxyCreator() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("系统不存在[{}]的Bean，创建类型为'{}'的Bean到容器。", AbstractAdvisorAutoProxyCreator.class.getName(),
				AnnotationAwareAspectJAutoProxyCreator.class.getName());
		}
		return new AnnotationAwareAspectJAutoProxyCreator();
	}
	
	/**
	 * 创建一个名为<code>yijiBoot_SF_I_IndexAnnotationAdvice</code>的 {@link IndexAnnotationAdvice} 到容器。
	 * @param depositBox 容器中的需要的 {@link DepositBox} 。
	 * @return 创建到容器的 {@link IndexAnnotationAdvice} 的实例。
	 */
	@Bean(name = IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "IndexAnnotationAdvice")
	@DependsOn(IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "DepositBox")
	@ConditionalOnProperty(value = { IndexProperties.PATH + ".enable" }, matchIfMissing = true)
	@ConditionalOnBean(DepositBox.class)
	public IndexAnnotationAdvice createIndexAnnotationAdvice(@Qualifier(IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX
																		+ "DepositBox") DepositBox depositBox) {
		boolean infoEnabled = LOGGER.isInfoEnabled();
		if (infoEnabled) {
			LOGGER.info("系统不存在[{}]的Bean，创建名为'{}'类型为'{}'的Bean到容器。", IndexAnnotationAdvice.class.getName(),
				IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "IndexAnnotationAdvice",
				IndexAnnotationAdvice.class.getName());
		}
		IndexAnnotationAdvice advice = new IndexAnnotationAdvice();
		AutoConfigurationPropertyUtils.safeCopyNotNullProperty(this.indexProperties, advice, "defaultSecurityUser");
		AutoConfigurationPropertyUtils.safeCopyNotNullProperty(this.indexProperties, advice, "logOriginal");
		AutoConfigurationPropertyUtils.safeCopyNotNullProperty(this.indexProperties, advice, "securityUserRef");
		advice.setDepositBox(depositBox);
		return advice;
	}
	
	/**
	 * 创建 IndexAnnotationAdvice 的PointcutAdvisor到容器，该PointcutAdvisor为所有 存在 {@link NeedIndex} 或者 {@link ReverseIndex} 注解。
	 * @param indexAnnotationAdvice 容器中的需要的 {@link IndexAnnotationAdvice} 。
	 * @return 创建的 PointcutAdvisor 。
	 */
	@Bean(name = IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "AspectJExpressionPointcutAdvisor")
	@ConditionalOnProperty(value = { IndexProperties.PATH + ".indexAllSensitiveMethod" }, matchIfMissing = true)
	@ConditionalOnBean(AspectJAwareAdvisorAutoProxyCreator.class)
	@ConditionalOnClass(name = "org.aspectj.weaver.tools.JoinPointMatch")
	public AspectJExpressionPointcutAdvisor createIndexAnnotationAdvicePointcutAdvisorByAnnotation(	@Qualifier(IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX
																												+ "IndexAnnotationAdvice") IndexAnnotationAdvice indexAnnotationAdvice) {
		String expression = "@within(com.yiji.boot.securityframework.annotation.Sensitive) || @annotation(com.yiji.boot.securityframework.annotation.Sensitive)";
		return createProtectAnnotationAdvicePointcutAdvisor(indexAnnotationAdvice, expression);
	}
	
	private AspectJExpressionPointcutAdvisor createProtectAnnotationAdvicePointcutAdvisor(	IndexAnnotationAdvice indexAnnotationAdvice,
																							String expression) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("创建保护相关的注解的Advice的PointcutAdvisor，类型为[{}]，AspectJ表达式为'{}'。",
				AspectJExpressionPointcutAdvisor.class, expression);
		}
		AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
		advisor.setAdvice(indexAnnotationAdvice);
		advisor.setExpression(expression);
		return advisor;
	}
	
	// yedis 设置
	
	@Bean(name = "cacheManager")
	@ConditionalOnProperty(name = IndexProperties.PATH + ".indexManagerEnv", havingValue = "domain",
			matchIfMissing = true)
	@ConditionalOnMissingBean(CacheManager.class)
	public CacheManager createSecurityYedisCacheManager(@Qualifier(IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX
																	+ "RedisTemplate") RedisTemplate<String, Map<String, String>> redisTemplate) {
		YedisCacheManager yedisCacheManager = new YedisCacheManager(redisTemplate);
		String expireTime = this.environment.getProperty("yiji.yedis.expireTime");
		if (expireTime != null) {
			yedisCacheManager.setDefaultExpiration(Long.parseLong(expireTime));
		}
		yedisCacheManager.setUsePrefix(true);
		return yedisCacheManager;
	}
	
	@Bean(name = IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "RedisTemplate")
	@ConditionalOnProperty(name = IndexProperties.PATH + ".indexManagerEnv", havingValue = "domain",
			matchIfMissing = true)
	public RedisTemplate<String, Map<String, String>> createSecurityRedisTemplate(	@Qualifier(IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX
																								+ "YedisConnectionFactory") YedisConnectionFactory yedisConnectionFactory) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("[domain]环境，系统不存在名为[{}]的Bean，创建类型为'{}'的Bean到容器。", IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX
																			+ "RedisTemplate",
				RedisTemplate.class.getName());
		}
		yedisConnectionFactory.setHostName(this.environment.getProperty("yiji.yedis.host"));
		String port = this.environment.getProperty("yiji.yedis.port");
		if (port != null) {
			yedisConnectionFactory.setPort(Integer.parseInt(port));
		}
		yedisConnectionFactory.setNamespace(CACHE_NAME_SPACE);
		yedisConnectionFactory.setPoolConfig(jedisPoolConfig());
		String timeout = this.environment.getProperty("yiji.yedis.timeOut");
		if (timeout != null) {
			yedisConnectionFactory.setTimeout(Integer.parseInt(timeout));
		}
		RedisTemplate<String, Map<String, String>> redisTemplate = new RedisTemplate<String, Map<String, String>>();
		redisTemplate.setConnectionFactory(yedisConnectionFactory);
		redisTemplate.setDefaultSerializer(new YedisSerializer<Object>());
		redisTemplate.setKeySerializer(new YedisStringKeySerializer());
		return redisTemplate;
	}
	
	@Bean(name = IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "YedisConnectionFactory")
	@ConditionalOnProperty(name = IndexProperties.PATH + ".indexManagerEnv", havingValue = "domain",
			matchIfMissing = true)
	public YedisConnectionFactory createScurityYedisConnectionFactory() {
		YedisConnectionFactory yedisConnectionFactory = new YedisConnectionFactory();
		yedisConnectionFactory.setHostName(this.environment.getProperty("yiji.yedis.host"));
		String port = this.environment.getProperty("yiji.yedis.port");
		if (port != null) {
			yedisConnectionFactory.setPort(Integer.parseInt(port));
		}
		yedisConnectionFactory.setNamespace(CACHE_NAME_SPACE);
		yedisConnectionFactory.setPoolConfig(jedisPoolConfig());
		String timeout = this.environment.getProperty("yiji.yedis.timeOut");
		if (timeout != null) {
			yedisConnectionFactory.setTimeout(Integer.parseInt(timeout));
		}
		return yedisConnectionFactory;
	}
	
	@Bean(name = IndexProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "GenericObjectPoolMetrics")
	@ConditionalOnMissingBean(GenericObjectPoolMetrics.class)
	@ConditionalOnProperty(name = IndexProperties.PATH + ".indexManagerEnv", havingValue = "domain",
			matchIfMissing = true)
	public GenericObjectPoolMetrics createSecurityRedisCacheConnPoolMetrics() {
		final ObjectName objectName;
		try {
			objectName = new ObjectName(JEDIS_CACHE_JMX_OBJECTNAME);
		} catch (MalformedObjectNameException e) {
			LOGGER.error("create ObjectName.", e);
			throw new IllegalArgumentException(e);
		}
		return new GenericObjectPoolMetrics(objectName, JEDIS_CACHE_JMX_PREFIX);
	}
	
	private JedisPoolConfig jedisPoolConfig() {
		String maxTotal = this.environment.getProperty("yiji.yedis.pool.maxTotal");
		String maxIdle = this.environment.getProperty("yiji.yedis.pool.maxIdle");
		String maxWait = this.environment.getProperty("yiji.yedis.pool.maxWait");
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		if (maxTotal != null) {
			jedisPoolConfig.setMaxTotal(Integer.parseInt(maxTotal));
		}
		if (maxIdle != null) {
			jedisPoolConfig.setMaxIdle(Integer.parseInt(maxIdle));
		}
		if (maxWait != null) {
			jedisPoolConfig.setMaxWaitMillis(Long.parseLong(maxWait));
		}
		jedisPoolConfig.setTestOnBorrow(true);
		jedisPoolConfig.setTestWhileIdle(true);
		jedisPoolConfig.setJmxNamePrefix(JEDIS_CACHE_JMX_PREFIX);
		return jedisPoolConfig;
	}
	
}
