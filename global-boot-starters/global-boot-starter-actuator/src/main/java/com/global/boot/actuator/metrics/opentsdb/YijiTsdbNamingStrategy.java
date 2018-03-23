/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-04-11 10:53 创建
 *
 */
package com.global.boot.actuator.metrics.opentsdb;

import com.google.common.base.Preconditions;
import org.springframework.boot.actuate.metrics.opentsdb.OpenTsdbName;
import org.springframework.boot.actuate.metrics.opentsdb.OpenTsdbNamingStrategy;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author daidai@yiji.com
 */
public class YijiTsdbNamingStrategy implements OpenTsdbNamingStrategy {
	private static final Pattern metricNameSepPattern = Pattern.compile("\\s+");
	private static final String tagSep = ",";
	private static final String keyValueSep = "=";
	
	@Override
	public OpenTsdbName getName(String metricName) {
		final Matcher matcher = metricNameSepPattern.matcher(metricName);
		if (matcher.find()) {
			int idx = matcher.start(0);
			Preconditions.checkState(idx > 0, "Invalid metricName! " + metricName);
			final OpenTsdbName tsdbName = new OpenTsdbName(metricName.substring(0, idx).trim());
			if (idx < metricName.length() - 1) {
				final String tags = metricName.substring(idx + 1).trim();
				if (StringUtils.hasText(tags)) {
					final String[] split = tags.split(tagSep);
					for (String s : split) {
						final String[] keyValue = s.split(keyValueSep);
						if (keyValue != null && keyValue.length == 2) {
							tsdbName.getTags().put(keyValue[0].trim(), keyValue[1].trim());
						} else {
							throw new IllegalStateException("Invalid metricName! " + metricName);
						}
					}
				}
			}
			return tsdbName;
		} else {
			return new OpenTsdbName(metricName.trim());
		}
	}
}
