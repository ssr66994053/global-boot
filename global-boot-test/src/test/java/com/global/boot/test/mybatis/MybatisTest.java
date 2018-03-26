/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-09-09 15:16 创建
 *
 */
package com.global.boot.test.mybatis;

import com.github.pagehelper.PageHelper;
import com.global.boot.test.TestBase;
import com.global.boot.test.mybatis.entity.City;
import com.global.boot.test.mybatis.mapper.CityMapper;
import com.global.boot.test.mybatis.mapper.CityMapperWithAnnotation;
import com.global.boot.test.mybatis.mapper.CityMapperWithXml;
import org.apache.ibatis.session.RowBounds;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CREATE TABLE `city` (`id` int(11) NOT NULL AUTO_INCREMENT,`name` varchar(255)
 * DEFAULT NULL,`age` INT DEFAULT NULL,PRIMARY KEY (`id`)) ENGINE=InnoDB
 * AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
 *
 * @author yanglie@yiji.com
 */
public class MybatisTest extends TestBase {
	
	@Autowired(required = false)
	private CityMapperWithAnnotation cityMapperWithAnnotation;
	@Autowired(required = false)
	private CityMapperWithXml cityMapperWithXml;
	
	@Autowired(required = false)
	private TestService testService;
	@Autowired(required = false)
	private CityMapper cityMapper;
	
	@Test
	public void testCommonMapper() throws Exception {
		cityMapper.deleteAll();
		String name = Long.toString(System.currentTimeMillis());
		City city = new City();
		city.setName(name);
		cityMapper.insert(city);
		assertThat(cityMapper.selectByPrimaryKey(city.getId())).isNotNull();
		cityMapper.deleteByPrimaryKey(city.getId());
		assertThat(cityMapper.selectAll()).isEmpty();
	}
	
	@Test
	public void testCommonMapper1() throws Exception {
		cityMapper.deleteAll();
		City city = new City();
		city.setName("abc");
		city.setAge(21);
		cityMapper.insert(city);
		City city1 = new City();
		city1.setName("acd");
		city1.setAge(22);
		cityMapper.insert(city1);
		
		City city2 = new City();
		city2.setName("abe");
		city2.setAge(23);
		cityMapper.insert(city2);
		//SELECT id,name,age FROM city WHERE ( age > ? and id <= ? ) or( name like ? )
		Example example = new Example(City.class);
		example.createCriteria().andGreaterThan("age", 21).andLessThanOrEqualTo("age", 22);
		example.or().andLike("name", "abe");
		assertThat(cityMapper.selectByExample(example)).hasSize(2);
		//SELECT id,name,age FROM city WHERE ( name like ? )
		Example example1 = new Example(City.class);
		example1.createCriteria().andLike("name", "ab%");
		assertThat(cityMapper.selectByExample(example1)).hasSize(2);
		cityMapper.deleteAll();
	}

	@Test
	public void testCommonMapper3() throws Exception {

		cityMapper.deleteAll();
		City city = new City();
		city.setName("ab");
		city.setAge(21);
		cityMapper.insert(city);
		City city1 = new City();
		city1.setName("ac");
		city1.setAge(22);
		cityMapper.insert(city1);

		City city2 = new City();
		city2.setName("abe");
		city2.setAge(22);
		cityMapper.insert(city2);

		City sc=new City();
		sc.setName("ab");
		//SELECT id,name,age FROM city WHERE name = ?
		assertThat(cityMapper.select(sc)).hasSize(1);
		City sc1=new City();
		sc1.setAge(22);
		sc1.setName("ac");
		//SELECT id,name,age FROM city WHERE name = ? AND age = ?
		assertThat(cityMapper.select(sc1)).hasSize(1);
		//SELECT id,name,age FROM city WHERE name = ? AND age = ? LIMIT 10
		assertThat(cityMapper.selectByRowBounds(sc1,new RowBounds(0, 10))).hasSize(1);
		PageHelper.startPage(1, 10);
		//SELECT id,name,age FROM city LIMIT 10
		assertThat(cityMapper.selectAll()).hasSize(3);

	}

	@Test
	public void testCommonMapper2() throws Exception {
		cityMapper.deleteAll();
		City city = new City();
		city.setName("abc");
		city.setAge(21);
		cityMapper.insert(city);
		City city1 = new City();
		city1.setName("acd");
		city1.setAge(22);
		cityMapper.insert(city1);
		cityMapper.deleteByPrimaryKeys(city.getId(), city1.getId());
		assertThat(cityMapper.selectAll()).isEmpty();
	}
	
	@Test
	public void testTransaction() {
		try {
			testService.test();
		} catch (Exception e) {
		}
	}
	
	@Test
	@Transactional
	public void testMapperAnnotation() {
		City city = new City();
		cityMapperWithAnnotation.delAll();
		city.setName("重庆");
		cityMapperWithAnnotation.addCity(city);
		city = cityMapperWithAnnotation.findCityById(city.getId());
		assertThat(city.getId()).isEqualTo(city.getId());
		cityMapperWithAnnotation.delCity(city.getId());
		city = cityMapperWithAnnotation.findCityById(city.getId());
		assertThat(city).isNull();
	}
	
	@Test
	@Transactional
	public void testMapperXml() {
		cityMapperWithXml.delAllXML();
		City city = new City();
		city.setName("重庆");
		cityMapperWithXml.addCity(city);
		city = cityMapperWithXml.findCityById(city.getId());
		assertThat(city.getId()).isEqualTo(city.getId());
		cityMapperWithXml.delCity(city.getId());
		city = cityMapperWithXml.findCityById(city.getId());
		assertThat(city).isNull();
		List<City> resultLst = cityMapperWithXml.findAll();
		assertThat(resultLst).isEmpty();
	}
	
	@Test
	@Transactional
	public void testPaging() {
		cityMapperWithXml.delAllXML();
		
		City city = new City();
		city.setName("重庆");
		cityMapperWithXml.addCity(city);
		
		city = new City();
		city.setName("北京");
		cityMapperWithXml.addCity(city);
		
		//这个查询不分页
		List<City> resultLst = cityMapperWithXml.findAll();
		assertThat(resultLst).isNotNull();
		
		//这个查询要分页
		PageHelper.startPage(1, 1);
		resultLst = cityMapperWithXml.findAll();
		assertThat(resultLst.size()).isEqualTo(1);
	}
	
	@Test
	@Transactional
	public void testLocalCacheIsClosed() throws Exception {
		long time = cityMapperWithAnnotation.currentTime().getTime();
		Thread.sleep(1000);
		assertThat(time).isNotEqualTo(cityMapperWithAnnotation.currentTime().getTime());
		
	}
}
