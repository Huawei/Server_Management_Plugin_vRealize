/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 */

package com.huawei.esight.service;

import static org.junit.Assert.fail;

import com.huawei.esight.service.bean.ServerDeviceBean;
import com.huawei.esight.service.bean.ServerDeviceDetailBean;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * ESightServiceImpl单元测试类.
 *
 * @author harbor
 * @since 2017/9/22
 */
public class ESightServiceImplTest {
    private final Logger logger = Logger.getLogger(ESightServiceImplTest.class);

    /**
     * 测试获取服务器信息方法
     */
    @Test
    public void testGetServerDetail() {
        ESightService service = new ESightServiceImpl();
        service.setLogger(Logger.getLogger(ESightServiceImpl.class));
        InputStream inputStream = null;
        try {
            Properties configProperties = new Properties();
            inputStream = this.getClass().getClassLoader().getResourceAsStream("unit-test.properties");
            configProperties.load(inputStream);

            String host = configProperties.getProperty("test.esight.ip");
            int port = Integer.parseInt(configProperties.getProperty("test.esight.port"));
            String account = configProperties.getProperty("test.esight.account");
            String eSightCode = configProperties.getProperty("test.esight.code");
            String openid = service.login(host, port, account, eSightCode);

            Assert.assertNotNull(openid);
            if (openid == null || openid.isEmpty()) {
                logger.error("Login FAIL!");
                fail("Login FAIL!");
            }

            String[] serverTypes = new String[] {"rack", "blade", "highdensity", "kunlun"};
            for (String serverType : serverTypes) {
                List<ServerDeviceBean> serverList = service.getServerDeviceList(serverType);
                Assert.assertNotNull(serverList);

                for (ServerDeviceBean bean : serverList) {
                    ServerDeviceDetailBean detailBean = service.getServerDetail(bean.getDn());
                    if (detailBean == null) {
                        logger.error("faile to fetch detail for device with dn ='" + bean.getDn() + "'");
                    }
                    Assert.assertNotNull(detailBean);
                }
            }
            service.logout(openid);
            logger.info("All Junit test PASSED!");
        } catch (IOException e) {
            logger.error("Call API ERROR", e);
            fail("Excetion throw found!");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error("close stream fail", e);
                }
            }
        }
    }
}