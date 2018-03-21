/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-04-20 16:31 创建
 *
 */
package com.yiji.boot.test.actuator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author daidai@yiji.com
 */
@Component
public class ConcurrentVisitCounterService {
	@Autowired(required = false)
	private CounterService counterService;
	@Autowired(required = false)
	private GaugeService gaugeService;
	private Random random = new Random();
	
	public void visit() {
		counterService.increment("visitCount");
		gaugeService.submit("visitGauge", random.nextDouble());
	}
}
