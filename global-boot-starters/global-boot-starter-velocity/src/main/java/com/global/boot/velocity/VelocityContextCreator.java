/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-16 10:33 创建
 *
 */
package com.global.boot.velocity;

import com.yjf.common.lang.exception.Exceptions;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.Toolbox;
import org.apache.velocity.tools.ToolboxFactory;
import org.apache.velocity.tools.config.XmlFactoryConfiguration;
import org.apache.velocity.tools.view.ViewToolContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.context.support.ServletContextResourceLoader;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author qiubo@yiji.com
 */
public class VelocityContextCreator {
	private Toolbox applicationToolbox;
	private Toolbox requestToolbox;
	
	private EventCartridge eventCartridge;
	
	public static final VelocityContextCreator INSTANCE = new VelocityContextCreator();
	
	private VelocityContextCreator() {
		this.eventCartridge = new EventCartridge();
		this.eventCartridge.addEventHandler(new YijiInvalidReferenceEventHandler());
		this.eventCartridge.addEventHandler(new HtmlEscapeReference());
	}
	
	protected Context createVelocityContext(VelocityEngine velocityEngine, String toolboxConfigLocation,
											ServletContext servletContext, Map<String, Object> model,
											HttpServletRequest request, HttpServletResponse response) throws Exception {
		ViewToolContext viewToolContext = new ViewToolContext(velocityEngine, request, response, servletContext);
		viewToolContext.putAll(model);
		if (toolboxConfigLocation != null) {
			if (requestToolbox != null) {
				//加载缓存对象
				viewToolContext.addToolbox(applicationToolbox);
				viewToolContext.addToolbox(requestToolbox);
			} else {
				ResourceLoader resourceLoader = new ServletContextResourceLoader(request.getServletContext());
				//从xml文件中加载toolbox
				XmlFactoryConfiguration cfg = new XmlFactoryConfiguration(true);
				cfg.read(resourceLoader.getResource(toolboxConfigLocation).getInputStream());
				ToolboxFactory toolboxFactory = cfg.createFactory();
				applicationToolbox = toolboxFactory.createToolbox(Scope.APPLICATION);
				viewToolContext.addToolbox(applicationToolbox);
				requestToolbox = toolboxFactory.createToolbox(Scope.REQUEST);
				viewToolContext.addToolbox(requestToolbox);
				
				//插件session scope toolbox，不允许使用
				Toolbox sessionToolbox = toolboxFactory.createToolbox(Scope.SESSION);
				if (!sessionToolbox.getToolClassMap().isEmpty()) {
					throw Exceptions
						.newRuntimeExceptionWithoutStackTrace("考虑到分布式session的原因，我们不支持session scope的velocity toolbox");
				}
			}
		}
		//添加异常事件处理
		VelocityContext velocityContext = new VelocityContext(viewToolContext);
		eventCartridge.attachToContext(velocityContext);
		return velocityContext;
	}
}
