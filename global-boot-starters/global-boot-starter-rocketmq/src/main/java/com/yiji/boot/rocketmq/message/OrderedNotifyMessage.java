/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-01-29 10:32 创建
 * qiubo@yiji.com 2016-02-22 18:06 增加tostring
 *
 */
package com.yiji.boot.rocketmq.message;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author yanglie@yiji.com
 * @author qiubo@yiji.com
 */
public class OrderedNotifyMessage extends NotifyMessage {
	@NotNull
	@Length(min = 1, max = 64)
	private String groupId;
	
	public String getGroupId() {
		return groupId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(100);
		sb.append("OrderedNotifyMessage{gid=");
		sb.append(this.getGid());
		sb.append(", id=").append(this.getId());
		sb.append(", topic=").append(this.getTopic());
		sb.append(", groupId=").append(this.getGroupId());
		sb.append(", event=").append(this.getEvent());
		sb.append(", fromSystem=").append(this.getFromSystem());
		sb.append(", toSystem=").append(this.getToSystem());
		sb.append(", env=").append(this.getEnv());
		sb.append(", data=").append(this.getData());
		sb.append('}');
		return sb.toString();
	}
}
