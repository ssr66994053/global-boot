/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-04 13:55 创建
 *
 */
package com.global.boot.test;

import com.yjf.common.lang.util.PasswdUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @author qiubo@yiji.com
 */
@Controller
public class GCController {
	
	@RequestMapping("/gc")
	@ResponseBody
	public void saveSessionValue() {
		PlayLoad playLoad = new PlayLoad();
		playLoad.check();
	}
	
	@Getter
	@Setter
	public static class PlayLoad {
		private byte[] bytes = new byte[2048];
		private String str = "23wsdsfdsdfdfdsdf";
		private int i = 193434;
		private long l = 122324l;
		private Date date = new Date();
		private String strq = null;
		
		public PlayLoad() {
			strq = PasswdUtil.genPasswd(20);
		}
		
		public void check() {
			Assert.notNull(strq);
		}
	}
	
}
