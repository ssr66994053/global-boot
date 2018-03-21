/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * LiXiang 2016年4月25日 下午8:58:41 创建
 */
package com.yiji.boot.test.security;

import com.yiji.common.security.annotation.Index;

import java.util.UUID;

/**
 *
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
public class TestEntity {
	
	private String id = UUID.randomUUID().toString();
	
	private String username;
	
	private String phone;
	
	private String testEntityIndex;
	
	public String getId() {
		return this.id;
	}
	
	/**
	 * 
	 */
	public TestEntity() {
	}
	
	public TestEntity(String username, String phone) {
		this.username = username;
		this.phone = phone;
	}
	
	@Index
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Index
	public String getPhone() {
		return this.phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getTestEntityIndex() {
		return this.testEntityIndex;
	}
	
	public void setTestEntityIndex(String testEntityIndex) {
		this.testEntityIndex = testEntityIndex;
	}
	
	/**
	 * @return
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TestEntity [username=" + this.username + ", phone=" + this.phone + ", testEntityIndex="
				+ this.testEntityIndex + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.phone == null) ? 0 : this.phone.hashCode());
		result = prime * result + ((this.username == null) ? 0 : this.username.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TestEntity other = (TestEntity) obj;
		if (this.phone == null) {
			if (other.phone != null) {
				return false;
			}
		} else if (!this.phone.equals(other.phone)) {
			return false;
		}
		if (this.username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!this.username.equals(other.username)) {
			return false;
		}
		return true;
	}
	
}
