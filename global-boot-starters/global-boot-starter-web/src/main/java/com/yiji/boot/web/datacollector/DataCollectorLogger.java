/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-11-17 15:05 创建
 *
 */
package com.yiji.boot.web.datacollector;

import com.yjf.common.log.LoggerFactory;

/**
 * @author yanglie@yiji.com
 */
public class DataCollectorLogger {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DataCollectorLogger.class);
    public static void log(String jsonData) {
        logger.info(jsonData);
    }
}
