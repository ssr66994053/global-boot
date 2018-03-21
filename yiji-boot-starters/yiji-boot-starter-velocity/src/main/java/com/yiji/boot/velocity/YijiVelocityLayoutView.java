/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2014-12-30 22:36 创建
 *
 */
package com.yiji.boot.velocity;

import org.apache.velocity.context.Context;
import org.springframework.web.servlet.view.velocity.VelocityLayoutView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 1.优化toolbox加载，由于tool是无状态的，所以可以缓存
 * <p>
 * 2.鉴于分布式session的性能考虑，禁止使用session scope的toolbox
 * <p>
 * 3.支持从classpath中加载toolbox
 * <p>
 * 4.引用处理失败时打印warn日志
 * <p>
 *
 * @author qzhanbo@yiji.com
 */
public class YijiVelocityLayoutView extends VelocityLayoutView {
	
	private VelocityContextCreator velocityContextCreator;
	
	public YijiVelocityLayoutView() {
		this.velocityContextCreator = VelocityContextCreator.INSTANCE;
	}
	
	@Override
	protected Context createVelocityContext(Map<String, Object> model, HttpServletRequest request,
											HttpServletResponse response) throws Exception {
		return velocityContextCreator.createVelocityContext(getVelocityEngine(), getToolboxConfigLocation(),
			getServletContext(), model, request, response);
	}
	
}
