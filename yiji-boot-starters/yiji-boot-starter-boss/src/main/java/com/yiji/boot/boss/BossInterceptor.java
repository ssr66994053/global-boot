/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 *
 * 修订记录:
 * zhouxi@yiji.com 2015-09-15 15:41 创建
 *
 */

package com.yiji.boot.boss;

import com.yiji.boot.web.SpringHandlerInterceptor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhouxi@yiji.com
 */
@SpringHandlerInterceptor(excludePatterns = { "/css/**", "/js/**", "/images/**", "/services/**", "/resources/**" },
		includePatterns = { "/boss/**" })
public class BossInterceptor implements HandlerInterceptor {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
							ModelAndView modelAndView) throws Exception {
		//boss 页面不加x-frame-options
		response.setHeader("x-frame-options", "");
		Subject subject = SecurityUtils.getSubject();
		
		if (null != subject && null != modelAndView) {
			
			modelAndView.getModelMap().put("subject", subject);
		}
		
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
																														throws Exception {
		
	}
}
