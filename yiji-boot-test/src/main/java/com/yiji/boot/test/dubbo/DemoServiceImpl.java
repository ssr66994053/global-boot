/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-21 15:28 创建
 *
 */
package com.yiji.boot.test.dubbo;

import com.alibaba.dubbo.config.annotation.Service;
import com.yjf.common.concurrent.MonitoredThreadPool;
import com.yjf.common.lang.result.SingleValueResult;
import com.yjf.common.service.SingleValueOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author qiubo@yiji.com
 */
@Service(version = "1.5")
public class DemoServiceImpl extends AbstractService implements DemoService {
	private static final Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);
	@Autowired
	private MonitoredThreadPool monitoredThreadPool;
	
	@Override
	public String echo(String msg) {
//		logger.info("receive:{}", msg);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return msg + " ref:" + userService;
	}
	
	@Override
	public SingleValueResult<String> echo1(SingleValueOrder<String> order) {
		monitoredThreadPool.submit(() -> logger.info("in task"));
		monitoredThreadPool.execute(() -> logger.info("in task"));
		monitoredThreadPool.submit(() -> {
			logger.info("in task");
			return "a";
		});
		return SingleValueResult.from("ok");
	}
}
