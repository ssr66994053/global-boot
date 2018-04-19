/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 *
 * 修订记录:
 * zhouxi@yiji.com 2015-09-15 15:41 创建
 *
 */
package com.global.boot.test;

//import com.global.boot.boss.log.annotations.BossOperation;
import com.global.common.log.Logger;
import com.global.common.log.LoggerFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhouxi@yiji.com
 */
@RestController
public class ControllerTest {
	
	private Logger logger = LoggerFactory.getLogger(ControllerTest.class);
	
	@RequestMapping("/testFilter")
	public String testFilter() {

		return "noBoss";

	}

	@RequestMapping("/boss/yiji-boot-test/testPerm")
	public String testPression() {
		Subject subject = SecurityUtils.getSubject();
		if (subject.isPermitted("boot:test")) {
			return "pass";
		}
		return "denied";

	}

	@RequestMapping("/css/anno")
	public String testAnno() {
		return "anno";
	}

	@RequestMapping("/boss/yiji-boot-test/testLog")
//	@BossOperation(description = "测试日志收集", ignoreParameterList = { "sensitive" })
	public String testLog(ModelMap map, HttpServletRequest request, HttpServletResponse response, String userId,
							String payPassword) {
		logger.info("进入日志收集,{},{}", userId, payPassword);
		return "testLog";
	}

	@RequestMapping("/boss/yiji-boot-test/testLogIgnore")
//	@BossOperation(description = "测试日志收集2", ignoreParams = true)
	public String testLogIgnore(String userId) {
		logger.info("进入日志收集,{}", userId);
		return "testLog2";
	}

	@RequestMapping("/boss/yiji-boot-test/testLogNoAnnotation")
	public String testLogNoAnnotation(String userId) {
		logger.info("进入日志收集,{}", userId);
		return "testLog3";
	}

	@RequestMapping("/boss/yiji-boot-test/testLogIgnoreMethod")
//	@BossOperation(description = "", ignore = true)
	public String testLogIgnoreMethod(String userId) {
		logger.info("进入日志收集,{}", userId);
		return "testLog4";
	}

	@RequestMapping("/boss/yiji-boot-test/testLogGolbalIgnoreMethod")
	public String myHomePage(String userId) {
		logger.info("进入日志收集,{}", userId);
		return "testLog4";
	}

	@RequestMapping("/boss/yiji-boot-test/testXss")
	public String testXss(String contentWithHtml){
		logger.info("测试xss,{}", contentWithHtml);
		return contentWithHtml;
	}

	@RequestMapping("/testXss2")
	public String testNonBossXss(String contentWithHtml){
		logger.info("测试xss,{}", contentWithHtml);
		return contentWithHtml;
	}


}
