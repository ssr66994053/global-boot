/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-03-17 22:31 创建
 */
package com.global.boot.session;

import com.google.common.base.Strings;
import org.springframework.session.Session;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionManager;
import org.springframework.session.web.http.MultiHttpSessionStrategy;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 支持从url中获取session,由于CookieHttpSessionStrategy是final的,请原谅我拷贝代码
 * @author qiubo@yiji.com
 */
public class UrlAndCookieHttpSessionStrategy implements MultiHttpSessionStrategy, HttpSessionManager {
	private static final String SESSION_IDS_WRITTEN_ATTR = CookieHttpSessionStrategy.class.getName().concat(
		".SESSIONS_WRITTEN_ATTR");
	
	static final String DEFAULT_ALIAS = "0";
	
	static final String DEFAULT_SESSION_ALIAS_PARAM_NAME = "_s";
	
	private Pattern ALIAS_PATTERN = Pattern.compile("^[\\w-]{1,50}$");
	
	private String cookieName = "SESSION";
	
	private String sessionParam = DEFAULT_SESSION_ALIAS_PARAM_NAME;
	
	private boolean isServlet3Plus = isServlet3();
	
	public String getRequestedSessionId(HttpServletRequest request) {
		Map<String, String> sessionIds = getSessionIds(request);
		String sessionAlias = getCurrentSessionAlias(request);
		return sessionIds.get(sessionAlias);
	}
	
	public String getCurrentSessionAlias(HttpServletRequest request) {
		if (sessionParam == null) {
			return DEFAULT_ALIAS;
		}
		String u = request.getParameter(sessionParam);
		if (u == null) {
			return DEFAULT_ALIAS;
		}
		if (!ALIAS_PATTERN.matcher(u).matches()) {
			return DEFAULT_ALIAS;
		}
		return u;
	}
	
	public String getNewSessionAlias(HttpServletRequest request) {
		Set<String> sessionAliases = getSessionIds(request).keySet();
		if (sessionAliases.isEmpty()) {
			return DEFAULT_ALIAS;
		}
		long lastAlias = Long.decode(DEFAULT_ALIAS);
		for (String alias : sessionAliases) {
			long selectedAlias = safeParse(alias);
			if (selectedAlias > lastAlias) {
				lastAlias = selectedAlias;
			}
		}
		return Long.toHexString(lastAlias + 1);
	}
	
	private long safeParse(String hex) {
		try {
			return Long.decode("0x" + hex);
		} catch (NumberFormatException notNumber) {
			return 0;
		}
	}
	
	public void onNewSession(Session session, HttpServletRequest request, HttpServletResponse response) {
		Set<String> sessionIdsWritten = getSessionIdsWritten(request);
		if (sessionIdsWritten.contains(session.getId())) {
			return;
		}
		sessionIdsWritten.add(session.getId());
		
		Map<String, String> sessionIds = getSessionIds(request);
		String sessionAlias = getCurrentSessionAlias(request);
		sessionIds.put(sessionAlias, session.getId());
		Cookie sessionCookie = createSessionCookie(request, sessionIds);
		response.addCookie(sessionCookie);
	}
	
	@SuppressWarnings("unchecked")
	private Set<String> getSessionIdsWritten(HttpServletRequest request) {
		Set<String> sessionsWritten = (Set<String>) request.getAttribute(SESSION_IDS_WRITTEN_ATTR);
		if (sessionsWritten == null) {
			sessionsWritten = new HashSet<String>();
			request.setAttribute(SESSION_IDS_WRITTEN_ATTR, sessionsWritten);
		}
		return sessionsWritten;
	}
	
	private Cookie createSessionCookie(HttpServletRequest request, Map<String, String> sessionIds) {
		Cookie sessionCookie = new Cookie(cookieName, "");
		if (isServlet3Plus) {
			sessionCookie.setHttpOnly(true);
		}
		sessionCookie.setSecure(request.isSecure());
		sessionCookie.setPath(cookiePath(request));
		// TODO set domain?
		
		if (sessionIds.isEmpty()) {
			sessionCookie.setMaxAge(0);
			return sessionCookie;
		}
		
		if (sessionIds.size() == 1) {
			String cookieValue = sessionIds.values().iterator().next();
			sessionCookie.setValue(cookieValue);
			return sessionCookie;
		}
		StringBuffer buffer = new StringBuffer();
		for (Map.Entry<String, String> entry : sessionIds.entrySet()) {
			String alias = entry.getKey();
			String id = entry.getValue();
			
			buffer.append(alias);
			buffer.append(" ");
			buffer.append(id);
			buffer.append(" ");
		}
		buffer.deleteCharAt(buffer.length() - 1);
		
		sessionCookie.setValue(buffer.toString());
		return sessionCookie;
	}
	
	public void onInvalidateSession(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> sessionIds = getSessionIds(request);
		String requestedAlias = getCurrentSessionAlias(request);
		sessionIds.remove(requestedAlias);
		
		Cookie sessionCookie = createSessionCookie(request, sessionIds);
		response.addCookie(sessionCookie);
	}
	
	/**
	 * Sets the name of the HTTP parameter that is used to specify the session alias. If the value is null, then only a
	 * single session is supported per browser.
	 *
	 * @param sessionAliasParamName the name of the HTTP parameter used to specify the session alias. If null, then ony
	 * a single session is supported per browser.
	 */
	public void setSessionAliasParamName(String sessionAliasParamName) {
		this.sessionParam = sessionAliasParamName;
	}
	
	/**
	 * Sets the name of the cookie to be used
	 * @param cookieName the name of the cookie to be used
	 */
	public void setCookieName(String cookieName) {
		if (cookieName == null) {
			throw new IllegalArgumentException("cookieName cannot be null");
		}
		this.cookieName = cookieName;
	}
	
	/**
	 * Retrieve the first cookie with the given name. Note that multiple cookies can have the same name but different
	 * paths or domains.
	 * @param request current servlet request
	 * @param name cookie name
	 * @return the first cookie with the given name, or {@code null} if none is found
	 */
	private static Cookie getCookie(HttpServletRequest request, String name) {
		if (request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
		Cookie cookies[] = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (name.equals(cookie.getName())) {
					return cookie;
				}
			}
		}
		return null;
	}
	
	private static String cookiePath(HttpServletRequest request) {
		return request.getContextPath() + "/";
	}
	
	public Map<String, String> getSessionIds(HttpServletRequest request) {
		Cookie session = getCookie(request, cookieName);
		String sessionCookieValue = session == null ? "" : session.getValue();
		Map<String, String> result = new LinkedHashMap<String, String>();
		StringTokenizer tokens = new StringTokenizer(sessionCookieValue, " ");
		if (tokens.countTokens() == 1) {
			result.put(DEFAULT_ALIAS, tokens.nextToken());
			return result;
		}
		while (tokens.hasMoreTokens()) {
			String alias = tokens.nextToken();
			if (!tokens.hasMoreTokens()) {
				break;
			}
			String id = tokens.nextToken();
			result.put(alias, id);
		}
		getSessionIdFromRequestIfPossible(request, result);
		return result;
	}
	
	/**
	 * 从url中获取sessionid
	 */
	private void getSessionIdFromRequestIfPossible(HttpServletRequest request, Map<String, String> result) {
		if (result.isEmpty()) {
			String sessionIdFromParmas = request.getParameter(cookieName);
			if (!Strings.isNullOrEmpty(sessionIdFromParmas)) {
				result.put(DEFAULT_ALIAS, sessionIdFromParmas);
			}
		}
	}
	
	public HttpServletRequest wrapRequest(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute(HttpSessionManager.class.getName(), this);
		return request;
	}
	
	public HttpServletResponse wrapResponse(HttpServletRequest request, HttpServletResponse response) {
		return new MultiSessionHttpServletResponse(response, request);
	}
	
	class MultiSessionHttpServletResponse extends HttpServletResponseWrapper {
		private final HttpServletRequest request;
		
		public MultiSessionHttpServletResponse(HttpServletResponse response, HttpServletRequest request) {
			super(response);
			this.request = request;
		}
		
		@Override
		public String encodeRedirectURL(String url) {
			url = super.encodeRedirectURL(url);
			return UrlAndCookieHttpSessionStrategy.this.encodeURL(url, getCurrentSessionAlias(request));
		}
		
		@Override
		public String encodeURL(String url) {
			url = super.encodeURL(url);
			
			String alias = getCurrentSessionAlias(request);
			return UrlAndCookieHttpSessionStrategy.this.encodeURL(url, alias);
		}
	}
	
	public String encodeURL(String url, String sessionAlias) {
		String encodedSessionAlias = urlEncode(sessionAlias);
		int queryStart = url.indexOf("?");
		boolean isDefaultAlias = DEFAULT_ALIAS.equals(encodedSessionAlias);
		if (queryStart < 0) {
			return isDefaultAlias ? url : url + "?" + sessionParam + "=" + encodedSessionAlias;
		}
		String path = url.substring(0, queryStart);
		String query = url.substring(queryStart + 1, url.length());
		String replacement = isDefaultAlias ? "" : "$1" + encodedSessionAlias;
		query = query.replaceFirst("((^|&)" + sessionParam + "=)([^&]+)?", replacement);
		if (!isDefaultAlias && url.endsWith(query)) {
			// no existing alias
			if (!(query.endsWith("&") || query.length() == 0)) {
				query += "&";
			}
			query += sessionParam + "=" + encodedSessionAlias;
		}
		
		return path + "?" + query;
	}
	
	private String urlEncode(String value) {
		try {
			return URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Returns true if the Servlet 3 APIs are detected.
	 * @return
	 */
	private boolean isServlet3() {
		try {
			ServletRequest.class.getMethod("startAsync");
			return true;
		} catch (NoSuchMethodException e) {
		}
		return false;
	}
}