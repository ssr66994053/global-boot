/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-09-15 15:45 创建
 *
 */
package com.yiji.boot.test.fastdfs;

import com.yiji.boot.test.TestBase;
import com.yiji.framework.fastdfs.FastdfsClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yanglie@yiji.com
 */
public class FastdfsTest extends TestBase {
	@Autowired(required = false)
	private FastdfsClient fastdfsClient;
	
	@Test
	//@Ignore
	public void testFastdfsClient() throws Exception {
		URL fileUrl = this.getClass().getResource("/Koala.jpg");
		String groupName = "t1";
		File file = new File(fileUrl.getPath());
		String fileId = fastdfsClient.upload(file, groupName);
		assertThat(fileId).isNotEmpty();
		String url = fastdfsClient.getUrl(fileId);
		assertThat(url).isNotEmpty();
		Map<String, String> meta = new HashMap<>();
		meta.put("fileName", file.getName());
		boolean result = fastdfsClient.setMeta(fileId, meta);
		assertThat(result).isTrue();
		Map<String, String> meta2 = fastdfsClient.getMeta(fileId);
		assertThat(meta2).isNotNull();
		byte[] fData = fastdfsClient.downLoad(fileId);
		assertThat(fData.length).isGreaterThan(0);
		result = fastdfsClient.delete(fileId);
		assertThat(result).isTrue();
	}
}
