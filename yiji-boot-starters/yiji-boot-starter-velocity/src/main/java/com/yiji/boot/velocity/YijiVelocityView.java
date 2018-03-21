/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-16 10:41 创建
 *
 */
package com.yiji.boot.velocity;

import org.apache.velocity.context.Context;
import org.springframework.web.servlet.view.velocity.VelocityToolboxView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author qiubo@yiji.com
 */
public class YijiVelocityView extends VelocityToolboxView {
	private VelocityContextCreator velocityContextCreator;
	
	public YijiVelocityView() {
		this.velocityContextCreator = VelocityContextCreator.INSTANCE;
	}
	
	@Override
	protected Context createVelocityContext(Map<String, Object> model, HttpServletRequest request,
											HttpServletResponse response) throws Exception {
		return velocityContextCreator.createVelocityContext(getVelocityEngine(), getToolboxConfigLocation(),
			getServletContext(), model, request, response);
	}
}
