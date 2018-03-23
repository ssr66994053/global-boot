/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-04-11 09:22 创建
 *
 */
package com.yiji.boot.actuator.metrics.opentsdb;

import com.google.common.base.Preconditions;
import org.springframework.boot.actuate.metrics.opentsdb.OpenTsdbData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author daidai@yiji.com
 */
public class TsdbBatchData {
	private List<OpenTsdbData> dataList;
	private int capacity;
	private long start;
	
	public TsdbBatchData(int capacity) {
		Preconditions.checkState(capacity > 0);
		this.capacity = capacity;
		dataList = new ArrayList<>(capacity);
		start = System.currentTimeMillis();
	}
	
	public void add(OpenTsdbData data) {
		dataList.add(data);
	}
	
	public int size() {
		return dataList.size();
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	public long getAge() {
		return System.currentTimeMillis() - start;
	}
	
	public boolean isFull() {
		return dataList.size() >= capacity;
	}
	
	public List<OpenTsdbData> getDataList() {
		return Collections.unmodifiableList(dataList);
	}
}
