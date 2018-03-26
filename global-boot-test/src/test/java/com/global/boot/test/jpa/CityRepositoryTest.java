/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-02-25 11:21 创建
 */
package com.global.boot.test.jpa;

import com.global.boot.test.TestBase;
import com.global.boot.test.dal.City;
import com.global.boot.test.dal.CityRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author qiubo@yiji.com
 */
public class CityRepositoryTest extends TestBase {
	@Autowired(required = false)
	private CityRepository cityRepository;
	
	@Test
	public void testName() throws Exception {
		Long id = new Random().nextInt(10000) + 100000l;
		City city = new City();
		city.setId(id);
		city.setName("cq");
		cityRepository.save(city);
		City saved = cityRepository.findOne(id);
		assertThat(saved).isNotNull();
		cityRepository.delete(id);
		assertThat(cityRepository.findOne(id)).isNull();
	}
}
