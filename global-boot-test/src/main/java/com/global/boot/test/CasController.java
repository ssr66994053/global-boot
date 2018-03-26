package com.global.boot.test;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
* @author shuijing
*/
@Controller
public class CasController {

	@RequestMapping("/casShiro")
	@ResponseBody
	public String testcasShiro(HttpServletRequest request) {
		return "casShiro";
	}

	@RequestMapping("/images/anno")
	@ResponseBody
	public String testAnno() {
		return "pass";
	}

	@RequestMapping("/boss/anno")
	@ResponseBody
	public String testBoss() {
		return "pass";
	}


	@RequestMapping("/login")
	@ResponseBody
	public String testCasLoginUrl(String userName, HttpServletRequest request) {
		return "pass";
	}
	@RequestMapping("/casLogin")
	@ResponseBody
	public String testCasLogin(String userName, HttpServletRequest request) {
		Subject subject = SecurityUtils.getSubject();
		subject.login(new UsernamePasswordToken(userName, userName));
		return userName + " success";
	}

}
