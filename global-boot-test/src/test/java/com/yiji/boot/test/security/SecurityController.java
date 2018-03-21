/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * Agrael 2015年9月9日 下午12:11:17 创建
 */
package com.yiji.boot.test.security;

import com.yiji.common.security.annotation.ConvertBind;
import com.yiji.common.security.annotation.InjectBind;
import com.yiji.common.security.annotation.NeedProtect;
import com.yiji.common.security.annotation.ValidSignature;
import com.yiji.common.security.web.Redirect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 测试控制器1，主要用注解实现。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
@Controller
@RequestMapping("/sfTest1")
public class SecurityController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@ValidSignature
	@RequestMapping("/testValidSignature")
	@ResponseBody
	// http://127.0.0.1:8081/sfTest1/testValidSignature.htm?securityUser=ASign&signature=e9d302431d7c37fa315dfb929703e816&v1=asky1&v2=asky2&v3=asky3
	public String testValidSignature(String signature) {
		this.logger.info("testValidSignature：signature={}。", signature);
		return "success:验证成功，签名是：" + signature;
	}
	
	@ConvertBind
	@RequestMapping("/testConvertBind")
	@ResponseBody
	// 这个是请求testRedirect跳过来的
	public String testConvertBind(@InjectBind String v1, @InjectBind String v2, String v3) {
		this.logger.info("testConvertBind：v1={},v2={},v3={}。", v1, v2, v3);
		return "success:转换注入成功，内容是：v1 = " + v1 + ",v2=" + v2 + ",v3=" + v3;
	}
	
	@RequestMapping("/testRedirect")
	// 127.0.0.1:8081/sfTest1/testRedirect.htm?v1=asky1&v2=asky2&v3=asky3
	public Redirect testRedirect(String v1, String v2, String v3) {
		this.logger.info("testRedirect：v1={},v2={},v3={}。", v1, v2, v3);
		return Redirect.newBuilder("testConvertBind").setSecurityUser("A").setSignature(true)
			.addNeedEncryptParameter("v1", v1).addNeedEncryptParameter("v2", v2).addCommonParameter("v3", v3)
			.addCommonParameter("securityUser", "B").build();
	}
	
	@RequestMapping("/testNeedProtect")
	@ResponseBody
	// http://127.0.0.1:8081/sfTest1/testNeedProtect.htm?securityUser=AProtect&v1=asky&v2=asky
	public String testNeedProtect(@NeedProtect(root = true) String v1, String v2) {
		this.logger.info("testNeedProtect：v1={},v2={}。", v1, v2);
		return "success:保护成功，内容是：v1 = " + v1 + ",v2=" + v2;
	}
	
}
