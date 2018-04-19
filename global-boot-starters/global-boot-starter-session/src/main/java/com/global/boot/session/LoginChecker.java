/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-05-03 15:26 创建
 */
package com.global.boot.session;

import com.global.common.lang.ip.IPUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.ExpiringSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 防止用户多次登录
 * 
 * @author qiubo@yiji.com
 */
public class LoginChecker {
	
	public static final String PRFIX = LoginChecker.class.getSimpleName() + ":";
	
	private RedisTemplate redisTemplate;
	private long expiredTimeOut;
	
	public LoginChecker(RedisTemplate<String, ExpiringSession> redisTemplate, long expiredTimeOut) {
		this.redisTemplate = redisTemplate;
		this.expiredTimeOut = expiredTimeOut;
	}
	
	/**
	 * 检查用户是否登录,如果已经登录,抛出异常
	 */
	public void checkUserHasLogin(HttpServletRequest request, String username) throws UserHasLoginException {
		HttpSession httpSession = request.getSession();
		String sessionId = httpSession.getId();
		LoginInfo value = (LoginInfo) redisTemplate.opsForValue().get(PRFIX + username);
		if (value == null) {
			LoginInfo loginInfo = new LoginInfo();
			loginInfo.setIp(IPUtil.getIpAddr(request));
			loginInfo.setSessionId(sessionId);
			loginInfo.setUsername(username);
			loginInfo.setLoginTime(new Date().getTime());
			httpSession.setAttribute(PRFIX, username);
			redisTemplate.opsForValue().set(PRFIX + username, loginInfo, expiredTimeOut, TimeUnit.SECONDS);
		} else {
			//session 已经存在,且是新生成的会话id
			if (!value.getSessionId().equals(sessionId)) {
				httpSession.invalidate();
				throw new UserHasLoginException(username, new Date().getTime(), IPUtil.getIpAddr(request));
			}
		}
	}
	
	public static class LoginInfo {
		private String username;
		private long loginTime;
		private String ip;
		private String sessionId;
		
		public String getIp() {
			return ip;
		}
		
		public void setIp(String ip) {
			this.ip = ip;
		}
		
		public long getLoginTime() {
			return loginTime;
		}
		
		public void setLoginTime(long loginTime) {
			this.loginTime = loginTime;
		}
		
		public String getSessionId() {
			return sessionId;
		}
		
		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}
		
		public String getUsername() {
			return username;
		}
		
		public void setUsername(String username) {
			this.username = username;
		}
	}
}
