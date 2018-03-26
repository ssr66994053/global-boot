/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-02-03 17:17 创建
 *
 */
package com.global.boot.test.mybatis;

import com.global.boot.test.mybatis.entity.City;
import com.global.boot.test.mybatis.mapper.CityMapperWithAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yanglie@yiji.com
 */
@Service
public class TestServiceImp implements TestService {
	@Autowired(required = false)
	private CityMapperWithAnnotation cityMapperWithAnnotation;
	
	@Transactional(rollbackFor = Exception.class)
	public void test() throws Exception {
		City city = new City();
		city.setId(1L);
		city.setName("重庆");
		cityMapperWithAnnotation.addCity(city);
		
		dosometing();
	}
	
	private void dosometing() throws Exception {
		throw new Exception("test");
	}
	
	public void setCityMapperWithAnnotation(CityMapperWithAnnotation cityMapperWithAnnotation) {
		this.cityMapperWithAnnotation = cityMapperWithAnnotation;
	}
}
