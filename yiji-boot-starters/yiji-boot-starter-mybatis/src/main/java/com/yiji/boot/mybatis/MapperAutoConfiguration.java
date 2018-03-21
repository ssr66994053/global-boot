/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-13 10:22 创建
 */
package com.yiji.boot.mybatis;

import com.github.pagehelper.autoconfigure.MapperProperties;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;

import javax.annotation.PostConstruct;

/**
 * 当前spring版本不支持构造器注入，只能重写
 * @author qiubo@yiji.com
 */
@Configuration
@ConditionalOnBean(SqlSessionFactory.class)
@EnableConfigurationProperties(MapperProperties.class)
@AutoConfigureAfter(MybatisAutoConfiguration.class)
public class MapperAutoConfiguration {
    @Autowired
    private  SqlSessionFactory sqlSessionFactory;
    @Autowired
    private MapperProperties properties;

    @PostConstruct
    public void addPageInterceptor() {
        MapperHelper mapperHelper = new MapperHelper();
        mapperHelper.setConfig(properties);
        if (properties.getMappers().size() > 0) {
            for (Class mapper : properties.getMappers()) {
                mapperHelper.registerMapper(mapper);
            }
        } else {
            mapperHelper.registerMapper(Mapper.class);
        }
        mapperHelper.processConfiguration(sqlSessionFactory.getConfiguration());
    }
}

