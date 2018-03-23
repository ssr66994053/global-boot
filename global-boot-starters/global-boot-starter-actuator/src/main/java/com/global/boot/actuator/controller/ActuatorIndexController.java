/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-10-30 12:03 创建
 *
 */
package com.yiji.boot.actuator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author qiubo@yiji.com
 */
@Controller
@RequestMapping("/mgt")
public class ActuatorIndexController {
	@RequestMapping({ "" })
	public String index() {
		return "redirect:/mgt/index.html";
	}
}
