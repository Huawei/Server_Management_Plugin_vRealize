/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 */

package com.huawei.esight.service.bean;

import com.huawei.esight.service.ESightResponseDataObject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 获取服务器列表结果的映射类.
 *
 * @author harbor
 * @since 2017/9/22
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseServerDeviceListBean extends ESightResponseDataObject<ServerDeviceBean> { }
