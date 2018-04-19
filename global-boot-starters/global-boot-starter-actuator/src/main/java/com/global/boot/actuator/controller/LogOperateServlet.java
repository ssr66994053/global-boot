/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanqing@yiji.com 2016-06-16 09:51 创建
 *
 */
package com.global.boot.actuator.controller;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.global.common.lang.result.ViewResultInfo;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.logging.logback.LogbackLoggingSystem;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yanqing@yiji.com
 */
public class LogOperateServlet extends HttpServlet {
	
	@Override
	public void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse)
																								throws ServletException,
																								IOException {
		try (PrintWriter printWriter = servletResponse.getWriter()) {
			String pathInfo = servletRequest.getPathInfo();
			//修改日志级别
			if ("/changeLogLevel".equals(pathInfo)) {
				changeLogLevel(servletRequest, printWriter);
			} else if ("/getLoggers".equals(pathInfo)) {
				getLoggers(printWriter);
			} else {
				ViewResultInfo resultInfo = createViewResultInfo("not supported log operate", false, null);
				printWriter.write(JSON.toJSONString(resultInfo, true));
			}
		}
	}
	
	/**
	 * @param request
	 * @throws IOException
	 */
	private void changeLogLevel(HttpServletRequest request, PrintWriter printWriter) {
		String logLevelStr = request.getParameter("logLevel");
		String loggerName = request.getParameter("loggerName");
		if (StringUtils.isEmpty(logLevelStr) || StringUtils.isEmpty(loggerName)) {
			ViewResultInfo resultInfo = createViewResultInfo("logLevel or loggerName illegal", false, null);
			printWriter.print(JSON.toJSONString(resultInfo, true));
			return;
		}
		LogLevel logLevel;
		try {
			logLevel = LogLevel.valueOf(logLevelStr.toUpperCase());
		} catch (Exception e) {
			ViewResultInfo resultInfo = createViewResultInfo("the logLevel not right", false, null);
			printWriter.print(JSON.toJSONString(resultInfo, true));
			return;
		}
		LoggingSystem loggingSystem = LoggingSystem.get(ClassUtils.getDefaultClassLoader());
		loggingSystem.setLogLevel(loggerName, logLevel);
		
		ViewResultInfo resultInfo = createViewResultInfo("logLevel modified success", true, null);
		printWriter.print(JSON.toJSONString(resultInfo, true));
	}
	
	/**
	 *
	 * @param printWriter
	 */
	private void getLoggers(PrintWriter printWriter) {
        LoggingSystem loggingSystem = LoggingSystem.get(ClassUtils.getDefaultClassLoader());
		ILoggerFactory factory = StaticLoggerBinder.getSingleton().getLoggerFactory();
		ViewResultInfo resultInfo;
		if (loggingSystem instanceof LogbackLoggingSystem) {
			if (factory instanceof LoggerContext) {
				LoggerContext loggerContext = (LoggerContext) factory;
				List<Logger> loggers = loggerContext.getLoggerList();
				resultInfo = createViewResultInfo("success", true,
					loggers.stream().filter(logger -> logger.getLevel() != null).collect(Collectors.toList()));
				
			} else {
				resultInfo = createViewResultInfo("LoggerFactory is not a Logback LoggerContext", false, null);
			}
		} else {
			resultInfo = createViewResultInfo("current getLoggers only support logback", false, null);
		}
		printWriter.write(JSON.toJSONString(resultInfo, SerializerFeature.PrettyFormat,
			SerializerFeature.DisableCircularReferenceDetect));
	}
	
	/**
	 *
	 * @param message
	 * @param success
	 * @param data
	 * @return
	 */
	private ViewResultInfo createViewResultInfo(String message, boolean success, Object data) {
		ViewResultInfo viewResultInfo = new ViewResultInfo();
		viewResultInfo.setMessage(message);
		viewResultInfo.setSuccess(success);
		viewResultInfo.setData(data);
		return viewResultInfo;
	}
}
