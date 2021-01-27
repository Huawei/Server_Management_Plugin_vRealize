/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 */

package com.huawei.adapter.util;

import com.integrien.alive.common.adapter3.Logger;
import com.integrien.alive.common.adapter3.describe.AdapterDescribe;
import com.vmware.vrops.logging.AdapterLoggerFactory;

import java.io.File;

/**
 * ESightAdapter工具类.
 *
 * @author harbor
 * @since 2017/9/22
 */
public class ESightAdapterUtil {
    private final Logger logger;

    public ESightAdapterUtil(AdapterLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(ESightAdapterUtil.class);
    }

    /**
     * Method to return Adapter's home directory/folder.
     *
     * @return Adapter home folder path
     */
    public static String getAdapterHome() {
        String adapterHome = System.getProperty("ADAPTER_HOME");
        String collectorHome = System.getProperty("COLLECTOR_HOME");
        if (collectorHome != null) {
            adapterHome = collectorHome + File.separator + "adapters";
        }

        return adapterHome;
    }

    /**
     * Returns the adapters root folder.
     *
     * @return Adapter home folder path
     */
    public static String getAdapterFolder() {
        return getAdapterHome() + File.separator + "ESightAdapter" + File.separator;
    }

    /**
     * Returns the adapters conf folder.
     *
     * @return adapters conf folder
     */
    public static String getConfFolder() {
        return getAdapterFolder() + "conf" + File.separator;
    }

    /**
     * Method to return describe XML location including the file name. It first
     * checks if
     *
     * @return describe XML location
     */
    public static String getDescribeXmlLocation() {
        String describeXml = null;
        String adapterHome = System.getProperty("ADAPTER_HOME");
        if (adapterHome == null) {
            describeXml = System.getProperty("user.dir") + File.separator;
        } else {
            describeXml = getConfFolder();
        }
        return describeXml;
    }

    /**
     * 判断字符串是相等.
     *
     * @param a 比较字符a
     * @param b 比较字符b
     * @return 对比结果, true or false
     */
    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) {
            return true;
        }
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Utility Method to create Adapter describe. Instance of AdapterDescribe is
     * created using describe.xml kept under config folder.
     *
     * @return instance of AdapterDescribe class {@link AdapterDescribe}
     */
    public AdapterDescribe createAdapterDescribe() {
        assert (logger != null);
        AdapterDescribe adapterDescribe = AdapterDescribe.make(getDescribeXmlLocation() + "describe.xml");
        if (adapterDescribe == null) {
            logger.error("Unable to load adapter describe");
        } else {
            logger.info("Successfully loaded adapter");
        }
        return adapterDescribe;
    }
}
