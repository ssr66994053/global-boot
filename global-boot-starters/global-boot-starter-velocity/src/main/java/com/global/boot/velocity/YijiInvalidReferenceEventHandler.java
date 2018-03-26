/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-01-19 18:55 创建
 *
 */
package com.global.boot.velocity;

import org.apache.velocity.app.event.InvalidReferenceEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.util.introspection.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * velocity 引用处理失败时打印日志
 * @author qzhanbo@yiji.com
 */
public class YijiInvalidReferenceEventHandler implements InvalidReferenceEventHandler {
	private static final Logger logger = LoggerFactory.getLogger(YijiInvalidReferenceEventHandler.class);
	
	@Override
	public Object invalidGetMethod(Context context, String reference, Object object, String property, Info info) {
		return null;
	}
	
	@Override
	public boolean invalidSetMethod(Context context, String leftreference, String rightreference, Info info) {
		logger.warn("vm模版[{}]调用不合法的方法[{}]", info.getTemplateName(), leftreference);
		return false;
	}
	
	@Override
	public Object invalidMethod(Context context, String reference, Object object, String method, Info info) {
		logger.warn("vm模版[{}]调用不合法的方法[{}]", info.getTemplateName(), reference);
		return null;
	}
}
