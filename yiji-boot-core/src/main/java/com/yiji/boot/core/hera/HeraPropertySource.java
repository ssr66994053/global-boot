/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */
package com.yiji.boot.core.hera;

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-10 15:42 创建
 *
 */

import com.yiji.framework.hera.client.core.Hera;
import org.springframework.core.env.EnumerablePropertySource;

import java.util.Set;

/**
 * @author qiubo@yiji.com
 */
public class HeraPropertySource extends EnumerablePropertySource {
	public HeraPropertySource() {
		super("hera");
	}
	
	@Override
	public String[] getPropertyNames() {
		Set<String> strings = Hera.getContext().getCachedProperties().keySet();
		return strings.toArray(new String[strings.size()]);
	}
	
	@Override
	public boolean containsProperty(String name) {
		return Hera.getContext().getCachedProperties().containsKey(name);
	}
	
	@Override
	public Object getProperty(String name) {
		return Hera.getContext().getCachedProperties().get(name);
	}
}