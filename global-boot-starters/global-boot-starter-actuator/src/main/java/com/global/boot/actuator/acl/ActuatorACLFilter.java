/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-11-02 14:11 创建
 *
 */
package com.global.boot.actuator.acl;

import com.global.common.web.IntranetAccessor;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author qiubo@yiji.com
 */
public class ActuatorACLFilter implements Filter {
	private IntranetAccessor intranetAccessor = new IntranetAccessor();
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
																								ServletException {
		if (intranetAccessor.allow((HttpServletRequest) request, (HttpServletResponse) response)) {
			((HttpServletResponse) response).setHeader("simpleMode", "true");
			chain.doFilter(request, response);
		}
	}
	
	@Override
	public void destroy() {
		
	}
}
