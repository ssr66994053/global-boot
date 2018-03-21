/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-04-19 14:09 创建
 *
 */
package com.yiji.boot.fastdfs;

import com.yiji.framework.fastdfs.FastdfsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

import javax.annotation.Resource;

/**
 * @author yanglie@yiji.com
 */
public class FastdfsHealthIndicator extends AbstractHealthIndicator {
	@Resource(name = "fastdfsClient")
	FastdfsClient fastdfsClient;
    @Autowired
    private FastdfsProperties fastdfsProperties;

    @Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		try {
			fastdfsClient.healthCheck(fastdfsProperties.getHealthCheckGroup());
		} catch (Exception e) {
			builder.down(e);
			return;
		}
		builder.up();
	}
}