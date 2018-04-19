/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-25 16:11 创建
 *
 */
package com.global.boot.xss;

import com.global.common.web.XssConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qiubo@yiji.com
 */
@ConfigurationProperties("yiji.xss")
public class XSSProperties extends XssConfig {
	/**
	 * 是否启用xss过滤
	 */
	private boolean enable = true;
	
	public boolean isEnable() {
		return enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
}
