package com.yiji.boot.test.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Component
public class ShiroBean {
	
	@Component("shiroRealm")
	public static class TestRealm extends AuthorizingRealm {
		public TestRealm() {
		}
		
		@Override
		protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
			return null;
		}
		
		@Override
		protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
			UsernamePasswordToken t = (UsernamePasswordToken) token;
			AuthenticationInfo authInfo = new SimpleAuthenticationInfo(t.getUsername(), t.getPassword(),
				t.getUsername());
			return authInfo;
		}
	}
	
	@Controller
	public static class TestShiroController {
		@RequestMapping("/shiro")
		@ResponseBody
		public String testShiro(HttpServletRequest request) {
			return "shiro";
		}
		
		@RequestMapping("/doLogin")
		@ResponseBody
		public String testDoLogin(String userName, HttpServletRequest request) {
			
			Subject subject = SecurityUtils.getSubject();
			subject.login(new UsernamePasswordToken(userName, userName));
			return userName + " success";
		}
		
		@RequestMapping("/shiroFilterAlow")
		@ResponseBody
		public String testShiroFilterAlow(HttpServletRequest request) {
			return "filter";
		}
		
		@RequestMapping("/shiroFilterNotAlow")
		@ResponseBody
		public String testShiroFilterNotAlow(HttpServletRequest request) {
			return "filter";
		}
	}
	
}
