/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 */

package com.huawei.esight.service;

import com.huawei.adapter.util.ConvertUtils;
import com.huawei.esight.api.provider.DefaultOpenIdProvider;
import com.huawei.esight.api.provider.DestoryOpenIdProvider;
import com.huawei.esight.api.provider.OpenIdProvider;
import com.huawei.esight.api.rest.server.GetServerDeviceApi;
import com.huawei.esight.api.rest.server.GetServerDeviceDetailApi;
import com.huawei.esight.bean.Esight;
import com.huawei.esight.exception.NoOpenIdException;
import com.huawei.esight.service.bean.ChildBladeBean;
import com.huawei.esight.service.bean.Constant;
import com.huawei.esight.service.bean.ResponseServerDeviceDetailBean;
import com.huawei.esight.service.bean.ResponseServerDeviceListBean;
import com.huawei.esight.service.bean.ServerDeviceBean;
import com.huawei.esight.service.bean.ServerDeviceDetailBean;

import org.apache.log4j.Logger;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ESightService接口实现类.
 *
 * @author harbor
 * @since 2017/9/22
 */
public class ESightServiceImpl implements ESightService {
    OpenIdProvider openIdProvider = null;

    private Logger logger = null;

    // eSight服务器对象
    private Esight esightServer = null;

    @Override
    public String login(String host, int port, String username, String password) {
        esightServer = new Esight(host, port, username, password);
        openIdProvider = new DefaultOpenIdProvider(esightServer);
        return openIdProvider.provide();
    }

    @Override
    public List<ServerDeviceBean> getServerDeviceList(String servertype) {
        GetServerDeviceApi<String> api = new GetServerDeviceApi<String>(esightServer, openIdProvider);
        int pageNo = 1;
        List<ServerDeviceBean> result = new ArrayList<>();

        // 分页读取所有数据
        while (true) {
            String response = api.doCall(servertype, pageNo + "", Constant.PAGE_SIZE, String.class);
            if (logger.isInfoEnabled()) {
                logger.info(
                    "Json string for server type = '" + servertype + "', pageNo='" + pageNo + "' is :  " + response);
            }
            ResponseServerDeviceListBean bean = ConvertUtils.json2Object(response, ResponseServerDeviceListBean.class);
            if (bean == null) {
                break;
            }
            // 分页数据保存到result
            result.addAll(bean.getData());
            if (bean.getTotalPage() <= pageNo) {
                break;
            }
            pageNo++;
        }

        // 将子服务器数据保存到result
        List<ServerDeviceBean> childList = new ArrayList<>();
        for (ServerDeviceBean bean : result) {
            for (ChildBladeBean child : bean.getChildBlades()) {
                ServerDeviceBean deviceBean = new ServerDeviceBean();
                deviceBean.setDn(child.getDn());
                deviceBean.setIpAddress(child.getIpAddress());
                deviceBean.setVersion(child.getVersion());
                deviceBean.setLocation(child.getLocation());
                deviceBean.setChildBlade(true);
                childList.add(deviceBean);
            }
        }
        result.addAll(0, childList);
        // 结果排序
        Collections.sort(result, new ServerDeviceBean());
        return result;
    }

    @Override
    @Nullable
    public ServerDeviceDetailBean getServerDetail(String dn) {
        try {
            String response = new GetServerDeviceDetailApi<String>(esightServer, openIdProvider).doCall(dn,
                String.class);
            if (logger.isInfoEnabled()) {
                logger.info("Json string for device with dn = '" + dn + "' is: " + response);
            }
            ResponseServerDeviceDetailBean bean = ConvertUtils.json2Object(response,
                ResponseServerDeviceDetailBean.class);
            if (bean == null) {
                return null;
            }
            if (bean.getCode() == 0 && bean.getData().size() > 0) {
                return bean.getData().get(0);
            }
        } catch (HttpMessageNotReadableException e) {
            logger.error("Failed to fetch server detail with dn = " + dn, e);
            return null;
        }
        return null;
    }

    @Override
    public void logout(String openid) {
        DestoryOpenIdProvider api = new DestoryOpenIdProvider(esightServer);
        try {
            api.logout(openid);
        } catch (NoOpenIdException e) {
            logger.error("logout with openid '" + openid + "' failed.", e);
        }
    }

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
