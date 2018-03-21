/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * LiXiang 2016年4月25日 下午6:47:52 创建
 */
package com.yiji.boot.test.security;

import com.yiji.boot.test.TestBase;
import com.yjf.common.lang.beans.Copier;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 
 *
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
@Component
public class IndexTest extends TestBase {
	
	@Autowired(required = false)
	private TestRepository testRepository;
	
	@Bean
	public TestRepository createRepository() {
		return new TestRepository();
	}
	
	@Test
	public void test() throws Exception {
		//		System.out.println(this.testRepository);
		TestEntity entity = new TestEntity("沙加", "13808353022");
		TestEntity e = Copier.copy(entity, TestEntity.class);
		//		System.out.println(entity);
		this.testRepository.save(entity);
		//		System.out.println(entity);
		entity = this.testRepository.findById(entity.getId());
		//		System.out.println(entity);
		Assert.assertEquals(entity, e);
	}
	
}
