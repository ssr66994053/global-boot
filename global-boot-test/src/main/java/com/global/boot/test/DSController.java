/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-07-25 14:01 创建
 */
package com.global.boot.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author qiubo@yiji.com
 */
@Controller
public class DSController {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@RequestMapping("/ds")
	@ResponseBody
	public Object welcome(Map<String, Object> model) {
		List<Map<String, Object>> list = jdbcTemplate.queryForList("select 'x'");
		return list;
	}
}
