/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-10-11 11:38 创建
 */
package com.yiji.boot.test.appservice;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.yiji.boot.test.TestBase;
import com.yjf.common.id.GID;
import com.yjf.common.service.SingleValueOrder;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author qiubo@yiji.com
 */
@BenchmarkOptions(benchmarkRounds = 40000, warmupRounds = 1000, concurrency = 1)
public class AppServicePerfTest extends TestBase {
	@Rule
	public TestRule benchmarkRun = new BenchmarkRule();
	@Autowired(required = false)
	private AppServiceTestService appServiceTestService;
	@Autowired(required = false)
	private CommonServiceTestService commonServiceTestService;
	private static SingleValueOrder<AppDto> order;
	
	@BeforeClass
	public static void setUp() throws Exception {
		order = new SingleValueOrder<>();
		AppDto appDto = new AppDto();
		appDto.setA1("axx");
		order.setPlayload(appDto);
		order.setGid(GID.newGID());
		order.setPartnerId("00000000010000000001");
		order.setMerchOrderNo("1");
	}
	
	@Test
	public void test_AppService() throws Exception {
		appServiceTestService.test1(order);
	}
	
	@Test
	public void test_CommonService() throws Exception {
		commonServiceTestService.test1(order);
	}

}
