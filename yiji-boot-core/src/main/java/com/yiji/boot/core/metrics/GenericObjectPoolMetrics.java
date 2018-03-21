/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-30 10:00 创建
 *
 */
package com.yiji.boot.core.metrics;

import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author daidai@yiji.com
 */
public class GenericObjectPoolMetrics extends AbstractJmxMetrics {
	private Collection<String> attributes;
	
	public GenericObjectPoolMetrics(ObjectName objectName, String displayPrefix) {
		super(objectName, displayPrefix);
		Collection<String> attributes = new ArrayList<>();
		attributes.add("MinIdle");
		attributes.add("MaxIdle");
		attributes.add("NumActive");
		attributes.add("MaxTotal");
		attributes.add("NumWaiters");
		attributes.add("MeanBorrowWaitTimeMillis");
		attributes.add("MaxBorrowWaitTimeMillis");
		attributes.add("MaxWaitMillis");
		this.attributes = Collections.unmodifiableCollection(attributes);
	}
	
	@Override
	public Collection<String> getAttributeKeys() {
		return this.attributes;
	}
}
