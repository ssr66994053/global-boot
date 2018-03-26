/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-01-29 10:06 创建
 * qiubo@yiji.com 2016-02-22 18:06 修改属性访问\增加tostring\调用合适的工具类
 *
 */
package com.global.boot.rocketmq.message;

import com.google.common.collect.Maps;
import com.global.boot.core.Apps;
import com.yjf.common.env.Env;
import com.yjf.common.lang.constants.SplitConstants;
import com.yjf.common.lang.validator.Validators;
import com.yjf.common.util.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author yanglie@yiji.com
 * @author qiubo@yiji.com
 */
public class NotifyMessage {
	public final static String ALL = "ALL";
	public final static String SEPARATOR = SplitConstants.SEPARATOR_CHAR_UNDERLINE;
	/**
	 * 统一流水号gid
	 */
	@NotNull
	@Length(min = 35, max = 35)
	private String gid;
	/**
	 * 队列主题名字
	 */
	@NotNull
	@Length(min = 1, max = 64)
	private String topic;
	/**
	 * 消息唯一id
	 */
	@NotNull
	@Length(min = 1, max = 64)
	private String id;
	/**
	 * 需要接收此消息的系统，默认是所有系统
	 */
	@Length(min = 1, max = 64)
	private String toSystem = ALL;
	/**
	 * 发送此消息的系统，默认是当前系统名
	 */
	@ToString.Invisible
	@Length(min = 1, max = 64)
	private String fromSystem = Apps.getAppName();
	/**
	 * 消息的事件(业务)类型
	 */
	@NotNull
	@Length(min = 1, max = 64)
	private String event;
	/**
	 * 环境标识
	 */
	@ToString.Invisible
	@Length(min = 1, max = 32)
	private String env = Env.getEnv();
	
	/**
	 * 数据字段，key-value形式的数据存储
	 */
	@ToString.Invisible
	@NotNull
	private Map<String, Object> data;
	
	/**
	 * 添加键值对
	 */
	public void param(String key, Object value) {
		if (data == null) {
			data = Maps.newHashMap();
		}
		data.put(key, value);
	}
	
	public String getTopic() {
		return topic;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	public String getEvent() {
		return event;
	}
	
	public void setEvent(String event) {
		this.event = event;
	}
	
	public String getEnv() {
		return env;
	}
	
	public Map<String, Object> getData() {
		return data;
	}
	
	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public String getFromSystem() {
		return fromSystem;
	}

	public void setFromSystem(String fromSystem) {
		this.fromSystem = fromSystem;
	}

	public String getToSystem() {
		return toSystem;
	}
	
	public void setToSystem(String toSystem) {
		this.toSystem = toSystem;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTag() {
		StringBuilder stringBuilder = new StringBuilder(50);
		stringBuilder.append(fromSystem);
		stringBuilder.append(SEPARATOR);
		stringBuilder.append(toSystem);
		stringBuilder.append(SEPARATOR);
		stringBuilder.append(event);
		stringBuilder.append(SEPARATOR);
		stringBuilder.append(env);
		return stringBuilder.toString();
	}
	
	public void check() {
		Validators.checkJsr303(this);
	}
	
	public String getGid() {
		return gid;
	}
	
	public void setGid(String gid) {
		this.gid = gid;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(100);
		sb.append("NotifyMessage{gid=");
		sb.append(this.getGid());
		sb.append(", id=").append(this.getId());
		sb.append(", topic=").append(this.getTopic());
		sb.append(", event=").append(this.getEvent());
		sb.append(", fromSystem=").append(this.getFromSystem());
		sb.append(", toSystem=").append(this.getToSystem());
		sb.append(", env=").append(this.getEnv());
		sb.append(", data=").append(this.getData());
		sb.append('}');
		return sb.toString();
	}
}
