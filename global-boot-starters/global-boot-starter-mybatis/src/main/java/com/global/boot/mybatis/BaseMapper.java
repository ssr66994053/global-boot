/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-12 10:03 创建
 */
package com.global.boot.mybatis;

import com.global.boot.mybatis.mapper.DeleteAllMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * 单表操作父接口
 * @author qiubo@yiji.com
 */
public interface BaseMapper<T> extends Mapper<T>, DeleteAllMapper<T> {
}
