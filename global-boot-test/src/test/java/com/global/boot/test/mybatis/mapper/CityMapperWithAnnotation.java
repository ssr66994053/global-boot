/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-09-09 15:18 创建
 *
 */
package com.global.boot.test.mybatis.mapper;

import com.global.boot.test.mybatis.entity.City;
import org.apache.ibatis.annotations.*;

import java.util.Date;

/**
 * @author yanglie@yiji.com
 */
public interface CityMapperWithAnnotation {
	@Select("SELECT * FROM city WHERE id = #{cityId}")
	City findCityById(@Param("cityId") Long cityId);
	@SelectKey(statement="SELECT LAST_INSERT_ID() AS ID", keyProperty="id", before=false, resultType=long.class)
	@Insert("INSERT INTO city(id,name) VALUES(#{id},#{name})")
	void addCity(City city);
	
	@Delete("DELETE FROM city WHERE id=#{id}")
	void delCity(@Param("id") Long id);
	@Delete("DELETE FROM city")
	void delAll();
	
	@Select("SELECT NOW()")
	Date currentTime();
}
