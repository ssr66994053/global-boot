/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-14 16:11 创建
 *
 */
package com.global.boot.core.configuration;

import com.global.boot.core.EnvironmentHolder;
import com.global.common.env.Env;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 此配置仅用于生成ide提示，日志系统会参考此配置
 * @author qiubo@yiji.com
 */
@Configuration
@EnableConfigurationProperties({ LogAutoConfiguration.LogProperties.class })
public class LogAutoConfiguration {
	
	@ConfigurationProperties("yiji.log")
	public static class LogProperties {
		
		public static final String GID_KEY = "gid";
		
		//		public static final String LOG_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{0}:%L- %msg%n%xEx{full, ${ignoredStackFrames}}";
		//		/**
		//		 * log pattern same as {@link #LOG_PATTERN} with ANSI Escape sequences
		//		 * color support
		//		 */
		//		public static final String ANSI_LOG_PATTERN = "%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(%-5level) %clr([%thread]){faint} %clr(%logger{0}){cyan}%clr(:%L){faint}%clr(-){red}  %m%n%xEx{full, ${ignoredStackFrames}}";
		
		/**
		 * mdc log pattern with parameter gid
		 */
		public static final String MDC_LOG_PATTERN_WITH_GID = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{0}:%L-%X{"
																+ GID_KEY
																+ "}- %msg%n%xEx{${yiji.log.exLength}, ${yiji.log.ignoredStackFrames}}";
		
		public static final String ANSI_MDC_LOG_PATTERN_WITH_GID = "%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(%-5level) %clr([%thread]){faint} %clr(%logger{0}){cyan}%clr(:%L){faint}%clr(-){red}%X{"
																	+ GID_KEY
																	+ "}- %m%n%xEx{${yiji.log.exLength}, ${yiji.log.ignoredStackFrames}}";
		
		/**
		 * mdc log pattern with parameter gid(inner unique id ) and oid(outter unique id)
		 */
		public static final String MDC_LOG_PATTERN_WITH_GID_AND_OID = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{0}:%L-%X{"
																		+ GID_KEY
																		+ "}:%X{oid}- %msg%n%xEx{${yiji.log.exLength}, ${yiji.log.ignoredStackFrames}}";
		
		public static final String ANSI_MDC_LOG_PATTERN_WITH_GID_AND_OID = "%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(%-5level) %clr([%thread]){faint} %clr(%logger{0}){cyan}%clr(:%L){faint}%clr(-){red}%X{"
																			+ GID_KEY
																			+ "}:%X{oid}- %m%n%xEx{${yiji.log.exLength}, ${yiji.log.ignoredStackFrames}}";
		/**
		 * 是否启用控制台日志
		 */
		private boolean consoleEnable = true;
		
		/**
		 * appender pattern，只能使用LogPattern定义的三种，默认为LogPattern.COMMON
		 */
		private Pattern pattern = Pattern.COMMON;

		/**
		 * 把日志消息内容替换为其他字符串,配置格式为regex:replacement,此配置项在hera中配置不生效
         */
		private String msgReplace;

		public boolean isConsoleEnable() {
			return consoleEnable;
		}
		
		public void setConsoleEnable(boolean consoleEnable) {
			this.consoleEnable = consoleEnable;
		}
		
		public Pattern getPattern() {
			return pattern;
		}
		
		public void setPattern(Pattern pattern) {
			this.pattern = pattern;
		}
		
		public enum Pattern {
			COMMON(MDC_LOG_PATTERN_WITH_GID, ANSI_MDC_LOG_PATTERN_WITH_GID),
			MDC_WITH_GID(MDC_LOG_PATTERN_WITH_GID, ANSI_MDC_LOG_PATTERN_WITH_GID),
			MDC_WITH_GID_OID(MDC_LOG_PATTERN_WITH_GID_AND_OID, ANSI_MDC_LOG_PATTERN_WITH_GID_AND_OID);
			private String pattern;
			private String ansiPattern;
			
			Pattern(String pattern, String ansiPattern) {
				this.pattern = pattern;
				this.ansiPattern = ansiPattern;
			}
			
			public String getNonAnsiPattern() {
				return pattern;
			}
			
			public String getAnsiPattern() {
				return ansiPattern;
			}
			
			public String getPattern(boolean needAnsi) {
				return needAnsi ? ansiPattern : pattern;
			}
		}
		public String getMsgReplace() {
			return msgReplace;
		}

		public void setMsgReplace(String msgReplace) {
			this.msgReplace = msgReplace;
		}
		/**
		 * 从环境获取日志appender pattern
		 */
		public static String pattern() {
			return pattern(false);
		}
		
		/**
		 * 从环境获取日志appender pattern
		 */
		public static String pattern(boolean needAnsi) {
			RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(EnvironmentHolder.get(), "yiji.log.");
			Pattern pattern = resolver.getProperty("pattern", Pattern.class, Pattern.COMMON);
			//online remove extract thread stack info
			if (Env.isOnline()) {
				return pattern.getPattern(needAnsi).replace(":%L", "");
			}
			return pattern.getPattern(needAnsi);
		}
		
		/**
		 * 从环境获取是否启用console log
		 */
		public static Boolean consoleEnable() {
			//支持多种配置形势，比如yiji.log.consoleEnable、yiji.log.console-enable
			RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(EnvironmentHolder.get(), "yiji.log.");
			return resolver.getProperty("consoleEnable", Boolean.class, Boolean.TRUE);
		}
	}
}
