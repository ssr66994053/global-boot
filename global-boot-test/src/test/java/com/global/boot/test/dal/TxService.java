/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-02-25 17:31 创建
 */
package com.global.boot.test.dal;

import com.yjf.common.lang.result.SingleValueResult;
import com.yjf.common.service.SingleValueOrder;

/**
 * @author qiubo@yiji.com
 */
public interface TxService {
	SingleValueResult<Long> invoke(SingleValueOrder<String> nameOrder);
}
