/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-03-30 09:48 创建
 *
 */
package com.global.boot.core.metrics;

import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author daidai@yiji.com
 */
public class GenericKeyedObjectPoolMetrics extends AbstractJmxMetrics {
	private Collection<String> attributes;
	
	public GenericKeyedObjectPoolMetrics(ObjectName objectName, String displayPrefix) {
		super(objectName, displayPrefix);
		Collection<String> attributes = new ArrayList<>();
		attributes.add("MinIdlePerKey");
		attributes.add("MaxIdlePerKey");
		attributes.add("NumActive");
		attributes.add("NumActivePerKey");
		attributes.add("MaxTotal");
		attributes.add("MaxTotalPerKey");
		attributes.add("NumWaiters");
		attributes.add("NumWaitersByKey");
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
