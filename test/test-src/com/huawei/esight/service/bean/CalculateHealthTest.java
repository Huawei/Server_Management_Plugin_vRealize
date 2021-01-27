/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 */

package com.huawei.esight.service.bean;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试类.
 *
 * @author harbor
 * @since 2017/9/22
 */
public class CalculateHealthTest {
    /**
     * 健康状态测试类
     */
    @Test
    public void testCalculateHealth() {
        List<String> childStatusList = new ArrayList<>();
        Assert.assertTrue("OK".equals(calculate(childStatusList)));

        childStatusList.add("Unknown");
        Assert.assertTrue("Unknown".equals(calculate(childStatusList)));

        childStatusList.add("Warning");
        Assert.assertTrue("Warning".equals(calculate(childStatusList)));

        childStatusList.add("Immediate");
        Assert.assertTrue("Immediate".equals(calculate(childStatusList)));

        childStatusList.add("Critical");
        Assert.assertTrue("Critical".equals(calculate(childStatusList)));
    }

    String calculate(List<String> childStatusList) {
        String defaultStatus = "OK";
        for (String status : childStatusList) {
            switch (defaultStatus) {
                case "OK": {
                    defaultStatus = status;
                }
                break;
                case "Unknown": {
                    if ("Warning".equals(status) || "Immediate".equals(status) || "Critical".equals(status)) {
                        defaultStatus = status;
                    }
                }
                break;
                case "Warning": {
                    if ("Immediate".equals(status) || "Critical".equals(status)) {
                        defaultStatus = status;
                    }
                }
                break;
                case "Immediate": {
                    if ("Critical".equals(status)) {
                        defaultStatus = status;
                    }
                }
                break;
                case "Critical": {
                    break;
                }
                default: {
                    break;
                }
            }
        }
        return defaultStatus;
    }
}
