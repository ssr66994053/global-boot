/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-25 13:55 创建
 *
 */
package com.global.boot.csrf;

import com.google.common.base.Splitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;

/**
 * @author qiubo@yiji.com
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(value = "yiji.csrf.enable", matchIfMissing = true)
@EnableConfigurationProperties({ CsrfProperties.class })
public class CsrfAutoConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(CsrfAutoConfiguration.class);
	@Autowired
	private CsrfProperties csrfProperties;
	
	@Bean
	public Filter csrfFilter() {
		CookieCsrfTokenRepository tokenRepository = new CookieCsrfTokenRepository();
		CsrfFilter csrfFilter = new CsrfFilter(tokenRepository);
		csrfFilter.setRequireCsrfProtectionMatcher(new RequireCsrfProtectionMatcher(csrfProperties.getIgnoreUris()));
		CsrfAccessDeniedHandlerImpl csrfAccessDeniedHandler = new CsrfAccessDeniedHandlerImpl();
		csrfAccessDeniedHandler.setErrorPage("/error");
		csrfFilter.setAccessDeniedHandler(csrfAccessDeniedHandler);
		return csrfFilter;
	}
	
	/**
	 * 匹配需要csrf过滤的请求
	 */
	private static class RequireCsrfProtectionMatcher implements RequestMatcher {
		
		public static final String POST = "POST";
		private Iterable<String> ignoreAntPathMatcherPatterns = null;
		private AntPathMatcher antPathMatcher = new AntPathMatcher();
		
		public RequireCsrfProtectionMatcher(String csrfIgnoreUris) {
			if (StringUtils.hasText(csrfIgnoreUris)) {
				logger.info("csrf忽略uris:{}", csrfIgnoreUris);
				ignoreAntPathMatcherPatterns = Splitter.on(',').trimResults().omitEmptyStrings().split(csrfIgnoreUris);
			}
		}
		
		/**
		 *
		 * @return true=需要过滤 false=不需要过滤
		 */
		public boolean matches(HttpServletRequest request) {
			if (POST.equals(request.getMethod())) {
				if (ignoreAntPathMatcherPatterns != null) {
					String uri = request.getRequestURI();
					int idx = uri.indexOf(';');
					if (idx > -1) {
						uri = uri.substring(0, idx);
					}
					for (String ignoreAntPathMatcherPattern : ignoreAntPathMatcherPatterns) {
						if (antPathMatcher.match(ignoreAntPathMatcherPattern, uri)) {
							return false;
						}
					}
				}
				return true;
			} else {
				return false;
			}
		}
		
	}
	
}
