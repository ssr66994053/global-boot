/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-19 11:16 创建
 */
package com.global.boot.test.dubbo;

import com.global.boot.dubbo.cache.CacheMeta;
import com.global.boot.core.components.dubbo.DubboCache;
import com.global.boot.dubbo.cache.SpelKeyGenerator;
import com.yjf.common.service.OrderBase;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * @author qiubo@yiji.com
 */
public class SpelKeyGeneratorTest {
	
	@Test
	public void name() throws Exception {
		SpelKeyGenerator spelKeyGenerator = new SpelKeyGenerator();
		TestOrder testOrder = new TestOrder();
		testOrder.setGid("gid");
		testOrder.setMerchOrderNo("merchOrderNo");
		testOrder.setName("bohr");
		Object[] args = new Object[] { testOrder };
		CacheMeta cacheMeta = populate("doit", testOrder);
		
		String key = (String) spelKeyGenerator.key(cacheMeta, args);
		Assert.assertEquals(null, key);
		cacheMeta = populate("doit1", testOrder);
		key = (String) spelKeyGenerator.key(cacheMeta, args);
		Assert.assertEquals("gid", key);
		cacheMeta = populate("doit2", testOrder);
		key = (String) spelKeyGenerator.key(cacheMeta, args);
		Assert.assertEquals("gid.bohr", key);
	}
	
	private CacheMeta populate(String methodName, TestOrder order) {
		CacheMeta cacheMeta = new CacheMeta();
		cacheMeta.setParameterTypes(new Class[] { order.getClass() });
		Method method = null;
		try {
			method = Test1.class.getDeclaredMethod(methodName);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		cacheMeta.setMethod(method);
		cacheMeta.setDubboCache(method.getAnnotation(DubboCache.class));
		return cacheMeta;
	}
	
	private class Test1 {
		@DubboCache(key = "")
		void doit() {
			
		}
		
		@DubboCache(key = "#p0.gid")
		void doit1() {
			
		}
		
		@DubboCache(key = "#p0.gid+'.'+#p0.name")
		void doit2() {
			
		}
	}
	
	private class TestOrder extends OrderBase {
		private String name;
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
	}
}
