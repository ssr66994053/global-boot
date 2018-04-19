/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-10-10 20:20 创建
 */
package com.global.boot.test.appservice;

import com.global.common.id.GID;
import com.global.common.lang.result.ViewResultInfo;
import com.global.common.service.SingleValueOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author qiubo@yiji.com
 */
@RestController
@RequestMapping("app")
public class AppServiceTestContorller {

    @Autowired
    private AppServiceTestService appServiceTestService;

    @RequestMapping("/test")
    public ViewResultInfo test(AppDto appRequest) {
        return appServiceTestService.test(newSingleValueOrder(appRequest)).to();
    }

    @RequestMapping("/test1")
    public ViewResultInfo test1(AppDto appRequest) {
        return appServiceTestService.test1(newSingleValueOrder(appRequest)).to();
    }

    @RequestMapping("/test2")
    public ViewResultInfo test2(AppDto appRequest) {
        return appServiceTestService.test2(newSingleValueOrder(appRequest)).to();
    }

    @RequestMapping("/test3")
    public ViewResultInfo test3(AppDto appRequest) {
        return appServiceTestService.test3(newSingleValueOrder(appRequest)).to();
    }


    private SingleValueOrder<AppDto> newSingleValueOrder(AppDto appRequest) {
        SingleValueOrder<AppDto> order = new SingleValueOrder<>();
        order.setPlayload(appRequest);
        order.setGid(GID.newGID());
        order.setPartnerId("00000000010000000001");
        order.setMerchOrderNo("1");
        return order;
    }

}
