/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-07-21 00:42 创建
 */
package com.global.boot.core.log;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.global.boot.core.EnvironmentHolder;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author qiubo@yiji.com
 */
public class MessageReplaceConverter extends MessageConverter {
	private Pattern pattern;
	private String replacement;
	
	public MessageReplaceConverter() {
		String msgReplace = EnvironmentHolder.get().getProperty("yiji.log.msgReplace");
		if (!Strings.isNullOrEmpty(msgReplace)) {
			List<String> list = Splitter.on(':').splitToList(msgReplace);
			if (list.size() != 2) {
				System.err.print("日志替换配置[yiji.log.msgReplace]错误,格式为[regex:replacement],例如yiji.log.msgReplace=abc|123:***\n");
				System.exit(0);
			}
			String regex = list.get(0);
			pattern = Pattern.compile(regex);
			replacement = list.get(1);
		}
	}
	
	public String convert(ILoggingEvent event) {
		String msg = super.convert(event);
		if (msg != null && pattern != null) {
			msg = pattern.matcher(msg).replaceAll(replacement);
		}
		return msg;
	}
}
