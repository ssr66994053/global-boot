/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-04-24 01:11 创建
 */
package com.global.boot.actuator.metrics;

import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.*;

/**
 * @author qiubo@yiji.com
 */
public class GCMetrics implements PublicMetrics {
	private Set<String> youngGc = new HashSet<>(3);
	private Set<String> oldGc = new HashSet<>(3);
	
	public GCMetrics() {
		youngGc.add("PS Scavenge");
		youngGc.add("ParNew");
		youngGc.add("G1 Young Generation");
		oldGc.add("PS MarkSweep");
		oldGc.add("ConcurrentMarkSweep");
		oldGc.add("G1 Old Generation");
	}
	
	@Override
	public Collection<Metric<?>> metrics() {
		Collection<Metric<?>> result = new LinkedHashSet<>();
		long minorCount = 0L;
		long minorTime = 0L;
		long majorCount = 0L;
		long majorTime = 0L;
		long unknownCount = 0L;
		long unknownTime = 0L;
		
		Iterator iter = ManagementFactory.getGarbageCollectorMXBeans().iterator();
		while (iter.hasNext()) {
			GarbageCollectorMXBean gc = (GarbageCollectorMXBean) iter.next();
			long count = gc.getCollectionCount();
			if (count != -1L) {
				if (youngGc.contains(gc.getName())) {
					minorCount += count;
					minorTime += gc.getCollectionTime();
				} else if (oldGc.contains(gc.getName())) {
					majorCount += count;
					majorTime += gc.getCollectionTime();
				} else {
					unknownCount += count;
					unknownTime += gc.getCollectionTime();
				}
			}
		}
		result.add(new Metric<>("gc.minorTime", minorTime));
		result.add(new Metric<>("gc.minorCount", minorCount));
		result.add(new Metric<>("gc.majorCount", majorCount));
		result.add(new Metric<>("gc.majorTime", majorTime));
		if (unknownCount == 0) {
			result.add(new Metric<>("gc.unknownCount", unknownCount));
			result.add(new Metric<>("gc.unknownTime", unknownTime));
		}
		return result;
	}
}
