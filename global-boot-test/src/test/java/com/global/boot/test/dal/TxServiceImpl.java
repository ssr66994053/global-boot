/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-02-25 17:36 创建
 */
package com.global.boot.test.dal;

import com.alibaba.dubbo.config.annotation.Service;
import com.yjf.common.lang.result.SingleValueResult;
import com.yjf.common.service.SingleValueOrder;
import com.yjf.common.spring.ApplicationContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

/**
 * @author qiubo@yiji.com
 */
@Service(version = "1.5")
public class TxServiceImpl implements TxService {
	
	@Override
	@Transactional
	public SingleValueResult<Long> invoke(SingleValueOrder<String> nameOrder) {
		CityRepository cityRepository = ApplicationContextHolder.get().getBean(CityRepository.class);
		Long id = new Random().nextInt(10000) + 100000l;
		City city = new City();
		city.setId(id);
		city.setName("cq");
		cityRepository.save(city);
		cityRepository.findOne(id);
		cityRepository.delete(id);
		return SingleValueResult.from(id);
	}
}
