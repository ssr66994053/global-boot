/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-18 16:52 创建
 */
package com.global.boot.dubbo.cache;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * @author qiubo@yiji.com
 */
@Slf4j
public class SpelKeyGenerator implements KeyGenerator {
	private SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
	private ParameterNameDiscoverer paramDiscoverer = new DefaultParameterNameDiscoverer();
	private ConcurrentMap<ExpressionCacheKey, Expression> cache = Maps.newConcurrentMap();
	
	private static final Logger log = LoggerFactory.getLogger(SpelKeyGenerator.class);
	
	@Override
	public Object key(CacheMeta cacheMeta,Object[] args) {
		try {
			MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(cacheMeta, cacheMeta.getMethod(),
					args, paramDiscoverer);
			if (Strings.isNullOrEmpty(cacheMeta.getDubboCache().key())) {
				return null;
			}
			ExpressionCacheKey key = new ExpressionCacheKey(cacheMeta);
			Expression expression = cache.get(key);
			if (expression == null) {
				expression = spelExpressionParser.parseExpression(cacheMeta.getDubboCache().key());
				cache.putIfAbsent(key, expression);
			}
			return expression.getValue(context);
		} catch (Exception e) {
			log.warn("spel parse failure", e);
		}
		return null;
	}
	
	@Data
	private class ExpressionCacheKey {
		private Class targetClass;
		private Method method;
		private String exp;
		
		public ExpressionCacheKey(CacheMeta cacheMeta) {
			this.targetClass = cacheMeta.getTargetClass();
			this.method = cacheMeta.getMethod();
			this.exp = cacheMeta.getDubboCache().key();
		}
		
	}
}
