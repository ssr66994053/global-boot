/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-24 13:37 创建
 *
 */
package com.yiji.boot.test;

import com.yiji.boot.session.LoginChecker;
import com.yiji.boot.session.UserHasLoginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author yanglie@yiji.com
 */
@Controller
public class SessionController {
	
	@Autowired
	private LoginChecker loginChecker;
	
	@RequestMapping("/session_save")
	@ResponseBody
	public String saveSessionValue(String key, String value, HttpSession session) {
		session.setAttribute(key, value);
		if (key.equals("error")) {
			session.setAttribute("error", value);
			throw new RuntimeException("error");
		}
		String sessionClassName = session.getClass().toString();
		return sessionClassName;
	}
	
	@RequestMapping("/session_read")
	@ResponseBody
	public String readSessionValue(String key, HttpSession session) {
		String value = (String) session.getAttribute(key);
		return value;
	}
	
	@RequestMapping("/session_login")
	@ResponseBody
	public String login(String name, HttpServletRequest request) {
		try {
			loginChecker.checkUserHasLogin(request, name);
		} catch (UserHasLoginException e) {
			// user has login already, throw a UserHasLoginException
			return e.toString();
		}
		return "loginSuccess";
	}
	
	@RequestMapping("/session_loginout")
	@ResponseBody
	public String loginout(String name, HttpServletRequest request) {
		request.getSession(false).invalidate();
		return "loginSuccess";
	}
	
}
