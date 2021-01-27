/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 */

package com.huawei.adapter.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * ConvertUtils单元测试类.
 *
 * @author harbor
 * @since 2017/9/22
 */
public class UtilsTest {
    /**
     * 测试健康状态
     */
    @Test
    public void testConvertHealthState() {
        assertEquals("Normal", ConvertUtils.convertHealthState(0));
        assertEquals("Offline", ConvertUtils.convertHealthState(-1));
        assertEquals("Unknown", ConvertUtils.convertHealthState(-2));
        assertEquals("Faulty", ConvertUtils.convertHealthState(-3));
        assertEquals("Faulty", ConvertUtils.convertHealthState(898));
    }

    /**
     * 测试功率
     */
    @Test
    public void testConvertPower() {
        assertEquals("500", ConvertUtils.convertPower("500.0 W"));
    }
}
