/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-21 15:28 创建
 *
 */
package com.yiji.boot.test.dubbo1;

import com.alibaba.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qiubo@yiji.com
 */
@Service(version = "1.5")
public class DemoServiceImpl1 extends AbstractService implements DemoService {
	private static final Logger logger = LoggerFactory.getLogger(DemoServiceImpl1.class);
	
	@Override
	public String echo(String msg) {
		logger.info("receive:{}", msg);
		return msg + " ref:" + userService;
	}
}
