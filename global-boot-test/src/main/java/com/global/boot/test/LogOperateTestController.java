/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanqing@yiji.com 2016-06-16 13:57 创建
 *
 */
package com.global.boot.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author yanqing@yiji.com
 */
@Controller
public class LogOperateTestController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("logLevelTest")
    @ResponseBody
    public void logLevelTest(){
        logger.info("hello");
        logger.debug("world");
    }
}
