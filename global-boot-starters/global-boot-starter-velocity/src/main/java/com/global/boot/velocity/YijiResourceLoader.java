/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-01-19 20:16 创建
 */
package com.yiji.boot.velocity;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.ui.velocity.SpringResourceLoader;

import java.io.InputStream;

/**
 * 修复vm返回路径中以/开头的路径,在本地测试时能读取到,但是在jar包中不能读取到的bug
 * @author qiubo@yiji.com
 */
public class YijiResourceLoader extends SpringResourceLoader {
	
	@Override
	public InputStream getResourceStream(String source) throws ResourceNotFoundException {
		if (source.startsWith("/")) {
			source = source.substring(1);
		}
		return super.getResourceStream(source);
	}
}
