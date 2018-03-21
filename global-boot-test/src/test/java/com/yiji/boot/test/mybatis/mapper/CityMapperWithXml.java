/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-09-09 17:17 创建
 *
 */
package com.yiji.boot.test.mybatis.mapper;

import com.yiji.boot.test.mybatis.entity.City;

import java.util.List;

/**
 * @author yanglie@yiji.com
 */
public interface CityMapperWithXml {
	void addCity(City city);
	
	City findCityById(Long cityId);
	
	List<City> findAll();
	
	void delCity(Long id);

	void delAllXML();
}
