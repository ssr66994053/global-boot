/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-02-25 17:39 创建
 */
package com.global.boot.test.jpa;

import com.alibaba.dubbo.config.annotation.Reference;
import com.global.boot.test.TestBase;
import com.global.boot.test.dal.TxService;
import com.global.common.lang.result.SingleValueResult;
import com.global.common.service.SingleValueOrder;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * @author qiubo@yiji.com
 */
public class TxServiceTest extends TestBase {
	
	@Reference(version = "1.5")
	private TxService txService;
	
	@Test
	public void testName() throws Exception {
		SingleValueOrder<String> singleValueOrder = new SingleValueOrder<>();
		singleValueOrder.setPlayload("xxxx");
		SingleValueResult<Long> result = txService.invoke(singleValueOrder);
		Assertions.assertThat(result).isNotNull();
	}
}
