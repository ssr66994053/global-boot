/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-21 17:32 创建
 *
 */
package com.global.boot.session;

import com.google.common.collect.Lists;
import com.global.boot.core.Apps;
import com.global.boot.core.metrics.GenericObjectPoolMetrics;
import com.global.framework.yedis.support.YedisConnectionFactory;
import com.global.framework.yedis.support.YedisSerializer;
import com.yjf.common.portrait.model.IOResource;
import com.yjf.common.portrait.model.TCPEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.session.ExpiringSession;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.redis.YijiRedisOperationsSessionRepository;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.SessionRepositoryFilter;
import redis.clients.jedis.JedisPoolConfig;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import java.util.EnumSet;
import java.util.List;

/**
 * @author yanglie@yiji.com
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(value = "yiji.session.enable", matchIfMissing = true)
@EnableConfigurationProperties({ SessionProperties.class })
public class YedisHttpSessionConfiguration implements IOResource<TCPEndpoint> {
	private static final String JEDIS_SESSION_JMX_OBJECTNAME = "org.apache.commons.pool2:type=GenericObjectPool,name=redis.session";
	private static final String JEDIS_SESSION_JMX_PREFIX = "redis.session";
	
	private static final Logger logger = LoggerFactory.getLogger(YedisHttpSessionConfiguration.class);
	
	@Autowired
	private SessionProperties sessionProperties;
	
	@Bean
	public RedisTemplate<String, ExpiringSession> sessionRedisTemplate(	@Qualifier("sessionConnectionFactory") RedisConnectionFactory sessionConnectionFactory) {
		RedisTemplate redisTemplate = new RedisTemplate();
		redisTemplate.setConnectionFactory(sessionConnectionFactory);
		redisTemplate.setDefaultSerializer(new YedisSerializer());
		return redisTemplate;
	}
	
	@Bean
	public LoginChecker sessions(	@Qualifier("sessionRedisTemplate") RedisTemplate<String, ExpiringSession> sessionRedisTemplate) {
		return new LoginChecker(sessionRedisTemplate, sessionProperties.getExpiredTimeOut());
	}
	
	@Bean
	public YijiRedisOperationsSessionRepository sessionRepository(	@Qualifier("sessionRedisTemplate") RedisTemplate<String, ExpiringSession> sessionRedisTemplate) {
		YijiRedisOperationsSessionRepository sessionRepository = new YijiRedisOperationsSessionRepository(
			sessionRedisTemplate);
		sessionRepository.setDefaultMaxInactiveInterval(sessionProperties.getExpiredTimeOut());
		return sessionRepository;
	}
	
	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(	@Qualifier("sessionConnectionFactory") RedisConnectionFactory sessionConnectionFactory) {
		DisabledRedisMessageListenerContainer container = new DisabledRedisMessageListenerContainer();
		return container;
	}
	
	@Bean
	public <S extends ExpiringSession> FilterRegistrationBean springSessionRepositoryFilter(SessionRepository<S> sessionRepository,
																							ServletContext servletContext) {
		
		SessionRepositoryFilter<S> sessionRepositoryFilter = new SessionRepositoryFilter<>(sessionRepository);
		sessionRepositoryFilter.setServletContext(servletContext);
		if (sessionProperties.isEnableUrlBasedSession()) {
			UrlAndCookieHttpSessionStrategy multiHttpSessionStrategy = new UrlAndCookieHttpSessionStrategy();
			multiHttpSessionStrategy.setCookieName(Apps.getAppSessionCookieName());
			sessionRepositoryFilter.setHttpSessionStrategy(multiHttpSessionStrategy);
		} else {
			CookieHttpSessionStrategy multiHttpSessionStrategy = new CookieHttpSessionStrategy();
			multiHttpSessionStrategy.setCookieName(Apps.getAppSessionCookieName());
			sessionRepositoryFilter.setHttpSessionStrategy(multiHttpSessionStrategy);
		}
		FilterRegistrationBean filter = new FilterRegistrationBean();
		filter.setFilter(sessionRepositoryFilter);
		filter.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
		filter.setOrder(SessionRepositoryFilter.DEFAULT_ORDER);
		filter.setAsyncSupported(true);
		return filter;
	}
	
	@Bean
	public GenericObjectPoolMetrics redisSessionConnPoolMetrics() {
		final ObjectName objectName;
		try {
			objectName = new ObjectName(JEDIS_SESSION_JMX_OBJECTNAME);
		} catch (MalformedObjectNameException e) {
			YedisHttpSessionConfiguration.logger.error("", e);
			throw new IllegalArgumentException(e);
		}
		return new GenericObjectPoolMetrics(objectName, JEDIS_SESSION_JMX_PREFIX);
	}
	
	@Override
	public List<TCPEndpoint> endpoints() {
		TCPEndpoint tcpEndpoint = new TCPEndpoint();
		tcpEndpoint.setName("session");
		tcpEndpoint.setPort(sessionProperties.getPort());
		tcpEndpoint.setIp(sessionProperties.getHost());
		return Lists.newArrayList(tcpEndpoint);
	}
	
	protected static class YedisConnectionConfiguration {
		@Autowired
		private SessionProperties sessionProperties;
		
		@Bean
		public RedisConnectionFactory sessionConnectionFactory() {
			YedisConnectionFactory yedisConnectionFactory = new YedisConnectionFactory();
			yedisConnectionFactory.setHostName(sessionProperties.getHost());
			yedisConnectionFactory.setPort(sessionProperties.getPort());
			yedisConnectionFactory.setNamespace(sessionProperties.getNamespace());
			yedisConnectionFactory.setPoolConfig(jedisPoolConfig());
			yedisConnectionFactory.setTimeout(sessionProperties.getConnectTimeOut());
			return yedisConnectionFactory;
		}
		
		private JedisPoolConfig jedisPoolConfig() {
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			jedisPoolConfig.setMaxTotal(sessionProperties.getPool().getMaxTotal());
			jedisPoolConfig.setMaxIdle(sessionProperties.getPool().getMaxIdle());
			jedisPoolConfig.setMaxWaitMillis(sessionProperties.getPool().getMaxWait());
			jedisPoolConfig.setTestOnBorrow(false);
			jedisPoolConfig.setTestWhileIdle(true);
			jedisPoolConfig.setTestOnReturn(false);
			jedisPoolConfig.setJmxNamePrefix(JEDIS_SESSION_JMX_PREFIX);
			return jedisPoolConfig;
		}
		
		@Bean
		public SessionYedisHealthIndicator sessionYedisHealthIndicator() {
			return new SessionYedisHealthIndicator();
		}
	}
}
