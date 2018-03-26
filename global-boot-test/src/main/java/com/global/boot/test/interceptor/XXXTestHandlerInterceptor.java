/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-04-28 18:32 创建
 */
package com.global.boot.test.interceptor;

import com.global.boot.web.SpringHandlerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author qiubo@yiji.com
 */
@SpringHandlerInterceptor(excludePatterns = { "/css/**", "/js/**", "/images/**", "/resources/**" },
		includePatterns = { "/**" })
public class XXXTestHandlerInterceptor implements HandlerInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(XXXTestHandlerInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		logger.info("{}", handler);
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			TestLog testLog = handlerMethod.getMethod().getAnnotation(TestLog.class);
			if (testLog != null) {
				logger.info("访问:{}", testLog.desc());
			}
		}
		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
							ModelAndView modelAndView) throws Exception {
		logger.info("{}", handler);

	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
																														throws Exception {
		
	}
}
