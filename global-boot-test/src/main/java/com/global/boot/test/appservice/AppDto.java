/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-10-11 12:10 创建
 */
package com.global.boot.test.appservice;

import com.global.common.lang.dto.DtoBase;
import com.global.common.service.OrderCheckException;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author qiubo@yiji.com
 */
public class AppDto extends DtoBase{

    @NotEmpty(groups = Test1.class)
    private String a1;
    @NotEmpty
    private String a2;
    @NotEmpty(groups = Test2.class)
    private String a3;
    @NotEmpty(groups = { Test1.class, Test2.class })
    private String a4;

    private String a5;

    interface Test1 {

    }

    interface Test2 {
    }

    /**
     * 当Test1校验组被执行时，调用此方法
     */
    public void checkOnTest1(OrderCheckException e) {
        if (a1 != null) {
            if (a1.startsWith("a")) {
                e.addError("a1", "不能以a开头");
            }
        }
    }

    /**
     * 当Test2校验组被执行时，调用此方法
     */
    public void checkOnTest2(OrderCheckException e) {
        //
    }

    public String getA1() {
        return a1;
    }

    public void setA1(String a1) {
        this.a1 = a1;
    }

    public String getA2() {
        return a2;
    }

    public void setA2(String a2) {
        this.a2 = a2;
    }

    public String getA3() {
        return a3;
    }

    public void setA3(String a3) {
        this.a3 = a3;
    }

    public String getA4() {
        return a4;
    }

    public void setA4(String a4) {
        this.a4 = a4;
    }

    public String getA5() {
        return a5;
    }

    public void setA5(String a5) {
        this.a5 = a5;
    }
}
