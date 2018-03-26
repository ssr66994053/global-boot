/**
 * Copyright (C) 2012 Ryan W Tenney (ryan@10e.us)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.global.boot.test.metrics;

import com.codahale.metrics.JmxReporter;
import com.global.boot.test.TestBase;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

@Ignore
public class MetricAnnotationTest extends TestBase {
	@Autowired(required = false)
	private MetricService metricService;
	@Resource(name = "methodGauge")
	private JmxReporter.JmxGaugeMBean methodGauge;
	@Resource(name = "cachedMethodGauge")
	private JmxReporter.JmxGaugeMBean cachedMethodGauge;
	@Resource(name = "fieldGauge")
	private JmxReporter.JmxGaugeMBean fieldGauge;
	@Resource(name = "timer")
	private JmxReporter.JmxTimerMBean timer;
	@Resource
	private JmxReporter.JmxCounterMBean counter;
	@Resource(name = "meter")
	private JmxReporter.JmxMeterMBean meter;
	
	@Test
	@Ignore
	public void testGaugeMethod() {
		Assert.assertEquals(metricService.gaugedMethod(), methodGauge.getValue());
	}
	
	@Test
	@Ignore
	public void testCachedGaugeMethod() {
		Assert.assertEquals(metricService.cachedGaugeMethod(), cachedMethodGauge.getValue());
	}
	
	@Test
	public void testGaugeField() {
		Assert.assertEquals(metricService.getGaugeValue(), fieldGauge.getValue());
	}
	
	@Test
	public void testCounterMethod() {
		long i = 0;
		while (i++ < 5) {
			metricService.countedMethod();
			Assert.assertEquals(i, counter.getCount());
		}
	}
	
	@Test
	public void testTimerMethod() {
		long i = 0;
		while (i++ < 5) {
			metricService.timedMethod();
			Assert.assertEquals(i, timer.getCount());
		}
		Assert.assertTrue(timer.getMax() > 0);
	}
	
	@Test
	public void testMeter() {
		metricService.meteredMethod();
		Assert.assertEquals(1, meter.getCount());
	}
}
