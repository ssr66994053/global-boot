/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-25 11:26 创建
 *
 */
package com.global.boot.csrf;

import com.alibaba.fastjson.JSON;
import com.global.common.lang.result.ViewResultInfo;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author qiubo@yiji.com
 */
public class CsrfAccessDeniedHandlerImpl implements AccessDeniedHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(CsrfAccessDeniedHandlerImpl.class);
	
	private static final String ACCEPT_HEADER = "Accept";
	
	private String errorPage = "/error";
	
	public void handle(HttpServletRequest request, HttpServletResponse response,
						AccessDeniedException accessDeniedException) throws IOException, ServletException {
		if (!response.isCommitted()) {
			logger.error("csrf校验异常,url={}", getRequestUrl(request), accessDeniedException);
			if (isJsonRequest(request)) {
				ViewResultInfo viewResultInfo = new ViewResultInfo();
				viewResultInfo.setSuccess(false);
				viewResultInfo.setCode("CSRF_DENIED");
				viewResultInfo.setMessage(StringEscapeUtils.escapeHtml(accessDeniedException.getMessage()));
				String jsonStr = JSON.toJSONString(viewResultInfo);
				response.setHeader("Content-Type","application/json");
				response.getWriter().write(jsonStr);
			} else {
				//cause of org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration.customize(),we can throw the exception
				throw accessDeniedException;
			}
		}
	}
	
	private String getRequestUrl(HttpServletRequest request) {
		return request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
	}
	
	private boolean isJsonRequest(HttpServletRequest request) {
		String uri = request.getRequestURI();
		if (uri.endsWith(".json")) {
			return true;
		}
		if (uri.endsWith(".html") || uri.endsWith(".htm")) {
			return false;
		}
		String acceptHeader = request.getHeader(ACCEPT_HEADER);
		try {
			if (StringUtils.hasText(acceptHeader)) {
				return acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE);
			}
		} catch (InvalidMediaTypeException ex) {
			return false;
		}
		return false;
	}
	
	/**
	 * The error page to use. Must begin with a "/" and is interpreted relative to the current context root.
	 *
	 * @param errorPage the dispatcher path to display
	 *
	 * @throws IllegalArgumentException if the argument doesn't comply with the above limitations
	 */
	public void setErrorPage(String errorPage) {
		if ((errorPage != null) && !errorPage.startsWith("/")) {
			throw new IllegalArgumentException("errorPage must begin with '/'");
		}
		
		this.errorPage = errorPage;
	}
}
