/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-09-09 15:17 创建
 *
 */
package com.yiji.boot.test.dal;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author yanglie@yiji.com
 */
@Entity
@Data
public class City {
    @Id
    private Long id;
    @Column(nullable = false)
    private String name;
}
