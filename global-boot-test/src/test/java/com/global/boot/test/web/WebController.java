/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-09-16 21:22 创建
 *
 */
package com.global.boot.test.web;

import com.yjf.common.lang.enums.Messageable;
import com.yjf.common.lang.util.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;

/**
 * @author qiubo@yiji.com
 */
@Controller
@RequestMapping("/web")
public class WebController {
	private static final Logger logger = LoggerFactory.getLogger(WebController.class);
	
	@RequestMapping("/testConverter")
	@ResponseBody
	public Pojo testConverter(Pojo pojo) {
		return pojo;
	}
	
	@RequestMapping("/testExceptionJson")
	@ResponseBody
	public Pojo testExceptionJson() {
		RuntimeException exception = new RuntimeException("xxx");
		throw exception;
	}
	
	@RequestMapping("/testExceptionJsonWithMessageable")
	@ResponseBody
	public Pojo testExceptionJsonWithMessageable() {
		throw new XXXException("message", "code");
	}
	
	@RequestMapping("/testExceptionHtml")
	public String testExceptionHtml() {
		throw new RuntimeException("xxx");
	}
	
	@RequestMapping("/testExceptionHtmlWithResponseStatus")
	public String testExceptionHtmlWithResponseStatus() {
		throw new ResponseStatusException();
	}
	
	public static class XXXException extends RuntimeException implements Messageable {
		
		private String code;
		
		public XXXException(String message, String code) {
			super(message);
			this.code = code;
		}
		
		@Override
		public String code() {
			return this.code;
		}
		
		@Override
		public String message() {
			return getMessage();
		}
	}
	
	@ResponseStatus(code = HttpStatus.BAD_GATEWAY, reason = ResponseStatusException.REASON)
	public static class ResponseStatusException extends RuntimeException {
		public static final String REASON = "xxxxx";
	}
	
	@RequestMapping("/testResponseBody")
	@ResponseBody
	public String testResponseBody() {
		return "hello";
	}
	
	@RequestMapping("/testResponseBodyPojo")
	@ResponseBody
	public Pojo testResponseBodyPojo() {
		Pojo pojo = new Pojo();
		pojo.setName("xxx");
		pojo.setMoney(new Money("1.0"));
		pojo.setDate(new Date());
		return pojo;
	}
}
