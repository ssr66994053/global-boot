package com.yiji.boot.test.shiro;

import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ShiroFilter extends AuthorizationFilter {
	
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
																											throws Exception {
		return alow;
	}
	
	private boolean alow;
	
	public void setAlow(boolean alow) {
		this.alow = alow;
	}
	
}
