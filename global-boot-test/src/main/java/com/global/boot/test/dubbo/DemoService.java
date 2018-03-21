/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-21 15:27 创建
 *
 */
package com.yiji.boot.test.dubbo;

import com.yjf.common.lang.result.SingleValueResult;
import com.yjf.common.service.SingleValueOrder;

/**
 * @author qiubo@yiji.com
 */
public interface DemoService {
	String echo(String msg);
	
	SingleValueResult<String> echo1(SingleValueOrder<String> order);
}
