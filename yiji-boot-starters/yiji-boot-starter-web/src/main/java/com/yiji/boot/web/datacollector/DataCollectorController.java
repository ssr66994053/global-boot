/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-11-17 15:16 创建
 *
 */
package com.yiji.boot.web.datacollector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @author yanglie@yiji.com
 */
@Controller
public class DataCollectorController {
	protected final Logger logger = LoggerFactory.getLogger(DataCollectorController.class);
	
	@RequestMapping("/xdata/collect")
	@ResponseBody
	public void collectData(String jsonData) {
		DataCollectorLogger.log(jsonData);
	}
	
	@RequestMapping("/xdata/fileCollect")
	@ResponseBody
	public void collectData(@RequestParam("file") MultipartFile file) {
		BufferedReader reader = null;
		try {
			String line;
			reader = new BufferedReader(new InputStreamReader(file.getInputStream(), Charset.forName("utf-8")));
			while ((line = reader.readLine()) != null) {
				DataCollectorLogger.log(line);
			}
		} catch (IOException e) {
			logger.error("获取数据埋点上传文件数据失败", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}
}
