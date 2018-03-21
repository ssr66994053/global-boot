/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-10-15 10:38 创建
 *
 */
package com.yiji.boot.web.common;

import com.yiji.boot.web.ResponseHeaderProperties;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author qiubo@yiji.com
 */
public class ResponseHeaderFilter extends OncePerRequestFilter {
	
	private ResponseHeaderProperties responseHeaderProperties;
	
	private String cacheString = null;
	
	private int cacheMaxAge = 0;
	
	public ResponseHeaderFilter(ResponseHeaderProperties responseHeaderProperties, int cacheMaxAge) {
		this.responseHeaderProperties = responseHeaderProperties;
		this.cacheString = "max-age=" + cacheMaxAge;
		this.cacheMaxAge = cacheMaxAge;
	}
	
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
																												throws IOException,
																												ServletException {
		if (responseHeaderProperties != null) {
			for (Map.Entry<String, String> entry : responseHeaderProperties.getHeaders().entrySet()) {
				response.setHeader(entry.getKey(), entry.getValue());
			}
			//            if (request.getRequestURI().startsWith("/boss")) {
			//				response.setHeader("x-frame-options", "");
			//			}
		}
		if (cacheMaxAge != -1) {
			String uri = request.getRequestURI();
			if (uri != null) {
				if (uri.endsWith(".js") || uri.endsWith(".css") || uri.endsWith(".png") || uri.endsWith(".jpg")
					|| uri.endsWith(".gif")) {
					if (!response.containsHeader("Cache-Control")) {
						response.setHeader("Cache-Control", cacheString);
					}
				}
			}
		}
		chain.doFilter(request, response);
	}
	
	@Override
	public void destroy() {
		
	}
}
