/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-01-13 22:48 创建
 */
package com.global.boot.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.Map;

/**
 * @author qiubo@yiji.com
 */
@Controller
public class JSPController {
	@RequestMapping("/jsp")
	public String welcome(Map<String, Object> model) {
		model.put("time", new Date());
		model.put("message", "xxxx");
		return "/templates/jsp/welcome.jsp";
	}
	
	@RequestMapping("/jsp1")
	public String welcome1(Map<String, Object> model) {
		model.put("time", new Date());
		model.put("message", "xxxx");
		return "welcome";
	}
}
