/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 */

package com.huawei.esight.service;

import com.huawei.esight.service.bean.ServerDeviceBean;
import com.huawei.esight.service.bean.ServerDeviceDetailBean;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * ESightService接口.
 *
 * @author harbor
 * @since 2017/9/22
 */
public interface ESightService {
    /**
     * eSight登录.
     *
     * @param host     ip
     * @param port     端口
     * @param username 用户名
     * @param password 密码
     * @return String
     */
    String login(String host, int port, String username, String password);

    /**
     * 服务器类型，范围(rack：机架服务器,blade：刀片服务器,highdensity：高密服务器).
     *
     * @param servertype type of server
     * @return list of ServerDeviceBean
     */
    List<ServerDeviceBean> getServerDeviceList(String servertype);

    /**
     * 根据dn (device name)获取服务器详细信息.
     *
     * @param dn device name
     * @return detail of server device
     */
    ServerDeviceDetailBean getServerDetail(String dn);

    /**
     * 注销openid.
     *
     * @param openid 会话ID
     */
    void logout(String openid);

    void setLogger(Logger logger);
}
