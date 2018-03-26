/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-15 14:12 创建
 *
 */
package com.global.boot.test.interceptor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiubo@yiji.com
 */
@Controller
@RequestMapping("/interceptor")
public class InterceptorController {
	
	@RequestMapping("/test")
	@ResponseBody
	@TestLog(desc = "xxxx")
	public String testIp(HttpServletRequest request) {
		return request.getRemoteAddr();
	}
	
}
