/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-20 14:03 创建
 *
 */
package com.yiji.boot.yedis;

import com.google.common.collect.Lists;
import com.yiji.boot.core.metrics.GenericObjectPoolMetrics;
import com.yiji.boot.yedis.spring.cache.YijiCachingConfigurer;
import com.yiji.framework.yedis.support.YedisCacheManager;
import com.yiji.framework.yedis.support.YedisConnectionFactory;
import com.yiji.framework.yedis.support.YedisSerializer;
import com.yiji.framework.yedis.support.YedisStringKeySerializer;
import com.yjf.common.portrait.model.IOResource;
import com.yjf.common.portrait.model.TCPEndpoint;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.List;

/**
 * @author yanglie@yiji.com
 */
@Configuration
@ConditionalOnProperty(value = "yiji.yedis.enable", matchIfMissing = true)
@EnableCaching
@EnableConfigurationProperties({ YedisProperties.class })
public class YedisAutoConfiguration implements IOResource<TCPEndpoint> {
	
	private static final String JEDIS_CACHE_JMX_OBJECTNAME = "org.apache.commons.pool2:type=GenericObjectPool,name=redis.cache";
	private static final String JEDIS_CACHE_JMX_PREFIX = "redis.cache";
	private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private YedisProperties yedisProperties;
	
	@Bean
	public CacheManager cacheManager(@Qualifier("redisTemplate") RedisTemplate redisTemplate,
										YedisProperties yedisProperties) {
		YedisCacheManager yedisCacheManager = new YedisCacheManager(redisTemplate);
		yedisCacheManager.setDefaultExpiration(yedisProperties.getExpireTime());
		yedisCacheManager.setUsePrefix(true);
		return yedisCacheManager;
	}
	
	@Bean
	public YijiCachingConfigurer yijiCachingConfigurer(RedisTemplate redisTemplate,YedisProperties yedisProperties) {
		return new YijiCachingConfigurer(redisTemplate, yedisProperties.getExpireTime());
	}
	
	@Bean
	public RedisTemplate redisTemplate(	@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate redisTemplate = new RedisTemplate();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setDefaultSerializer(new YedisSerializer());
		redisTemplate.setKeySerializer(new YedisStringKeySerializer());
		return redisTemplate;
	}
	
	@Bean
	public RedisConnectionFactory redisConnectionFactory(YedisProperties yedisProperties) {
		YedisConnectionFactory yedisConnectionFactory = new YedisConnectionFactory();
		yedisConnectionFactory.setHostName(yedisProperties.getHost());
		yedisConnectionFactory.setPort(yedisProperties.getPort());
		yedisConnectionFactory.setNamespace(yedisProperties.getNamespace());
		yedisConnectionFactory.setPoolConfig(jedisPoolConfig(yedisProperties));
		yedisConnectionFactory.setTimeout(yedisProperties.getTimeOut());
		return yedisConnectionFactory;
	}
	
	@Bean
	public GenericObjectPoolMetrics redisCacheConnPoolMetrics() {
		final ObjectName objectName;
		try {
			objectName = new ObjectName(JEDIS_CACHE_JMX_OBJECTNAME);
		} catch (MalformedObjectNameException e) {
			logger.error("", e);
			throw new IllegalArgumentException(e);
		}
		return new GenericObjectPoolMetrics(objectName, JEDIS_CACHE_JMX_PREFIX);
	}
	
	private JedisPoolConfig jedisPoolConfig(YedisProperties yedisProperties) {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(yedisProperties.getPool().getMaxTotal());
		jedisPoolConfig.setMaxIdle(yedisProperties.getPool().getMaxIdle());
		jedisPoolConfig.setMaxWaitMillis(yedisProperties.getPool().getMaxWait());
		jedisPoolConfig.setTestOnBorrow(false);
		jedisPoolConfig.setTestWhileIdle(true);
		jedisPoolConfig.setTestOnReturn(false);
		jedisPoolConfig.setJmxNamePrefix(JEDIS_CACHE_JMX_PREFIX);
		return jedisPoolConfig;
	}
	
	@Bean
	public YedisHealthIndicator yedisHealthIndicator() {
		return new YedisHealthIndicator();
	}
	
	@Override
	public List<TCPEndpoint> endpoints() {
		TCPEndpoint tcpEndpoint = new TCPEndpoint();
		tcpEndpoint.setName("cache");
		tcpEndpoint.setPort(yedisProperties.getPort());
		tcpEndpoint.setIp(yedisProperties.getHost());
		return Lists.newArrayList(tcpEndpoint);
	}
}
