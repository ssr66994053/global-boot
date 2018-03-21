/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-11-01 10:58 创建
 */
package com.yiji.boot.core.listener;

import com.yiji.boot.core.AppConfigException;
import com.yiji.boot.core.Apps;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author qiubo@yiji.com
 */
public class AppInfoWriter {
	public void write(String fileName, int info) {
		write(fileName, Integer.toString(info));
	}
	
	public void write(String fileName, String info) {
		Assert.hasLength(fileName);
		Assert.hasLength(info);
		String filePath = Apps.getLogPath() + fileName;
		File httpFile = new File(filePath);
		try {
			createParentFolder(httpFile);
			FileWriter writer = new FileWriter(httpFile);
			try {
				writer.append(info);
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			throw new AppConfigException("写信息[" + info + "]到" + filePath + "文件失败", e);
		}
		httpFile.deleteOnExit();
	}
	
	private void createParentFolder(File file) {
		File parent = file.getParentFile();
		if (parent != null) {
			parent.mkdirs();
		}
	}
}
