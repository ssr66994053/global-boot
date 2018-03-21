/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * LiXiang 2016年4月25日 下午8:58:13 创建
 */
package com.yiji.boot.test.security;

import com.yiji.boot.securityframework.annotation.Sensitive;
import com.yiji.common.security.annotation.NeedIndex;
import com.yiji.common.security.annotation.ReverseIndex;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
public class TestRepository {
	
	private Map<String, TestEntity> entitys = new HashMap<String, TestEntity>();
	
	@Sensitive
	public void save(@NeedIndex TestEntity entity) {
		this.entitys.put(entity.getId(), entity);
	}
	
	@Sensitive
	public @ReverseIndex TestEntity findById(String id) {
		return this.entitys.get(id);
	}
	
}
