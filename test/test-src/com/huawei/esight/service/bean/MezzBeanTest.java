/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 */

package com.huawei.esight.service.bean;

import org.junit.Assert;
import org.junit.Test;

/**
 * Mezz测试类.
 *
 * @author harbor
 * @since 2017/9/22
 */
public class MezzBeanTest {
    /**
     * 测试类
     */
    @Test
    public void testMacConvertion() {
        MezzBean bean = new MezzBean();
        bean.setMezzMac("0;1#666;2#test1;0#18:DE:D7:88:C3:3F;");
        Assert.assertArrayEquals("", "test1".getBytes(), bean.getMezzIBMac().getBytes());
        Assert.assertArrayEquals("", "18-DE-D7-88-C3-3F".getBytes(), bean.getMezzETHMac().getBytes());
        Assert.assertArrayEquals("", "666".getBytes(), bean.getMezzFCWWPN().getBytes());

        bean = new MezzBean();
        bean.setMezzMac("dummy-mac");
        Assert.assertArrayEquals("", "--".getBytes(), bean.getMezzIBMac().getBytes());
        Assert.assertArrayEquals("", "--".getBytes(), bean.getMezzETHMac().getBytes());
        Assert.assertArrayEquals("", "--".getBytes(), bean.getMezzFCWWPN().getBytes());
    }
}
