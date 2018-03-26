/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-07-25 14:01 创建
 */
package com.global.boot.test.thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author qiubo@yiji.com
 */
@Controller
@RequestMapping("/tl")
public class TLController {
	
	@RequestMapping("/test")
	public String  welcome(ModelMap modelMap) {
		modelMap.addAttribute("message","test");
		return "thymeleaf/test";
	}
}
