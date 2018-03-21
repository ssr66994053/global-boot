/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-11-21 16:16 创建
 */
package com.yiji.boot.test.dal;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author qiubo@yiji.com
 */
@Entity
@Table(name = "sms_template")
@Data
public class SmsTemplate {
    @Id
    private Long id;
    private String name;
    private String content;
    private Date rawAddTime = new Date();

    private Date rawUpdateTime = null;

    //访问url http://127.0.0.1:9832/api/smsTemplates/2?projection=noTime
//    @Projection(name = "noTime", types = {SmsTemplate.class})
    interface NoTime {
        String getName();

        String getContent();
    }

}
