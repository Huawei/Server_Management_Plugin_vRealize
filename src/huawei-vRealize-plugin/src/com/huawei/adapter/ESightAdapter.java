/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 */

package com.huawei.adapter;

import com.huawei.adapter.util.ConvertUtils;
import com.huawei.adapter.util.ESightAdapterUtil;
import com.huawei.esight.exception.EsightException;
import com.huawei.esight.service.ESightService;
import com.huawei.esight.service.ESightServiceImpl;
import com.huawei.esight.service.bean.BoardBean;
import com.huawei.esight.service.bean.CPUBean;
import com.huawei.esight.service.bean.ChildBladeBean;
import com.huawei.esight.service.bean.Constant;
import com.huawei.esight.service.bean.DiskBean;
import com.huawei.esight.service.bean.FanBean;
import com.huawei.esight.service.bean.MemoryBean;
import com.huawei.esight.service.bean.MezzBean;
import com.huawei.esight.service.bean.NetworkCardBean;
import com.huawei.esight.service.bean.PCIEBean;
import com.huawei.esight.service.bean.PSUBean;
import com.huawei.esight.service.bean.RAIDBean;
import com.huawei.esight.service.bean.ServerDeviceBean;
import com.huawei.esight.service.bean.ServerDeviceDetailBean;

import com.integrien.alive.common.adapter3.AdapterBase;
import com.integrien.alive.common.adapter3.DiscoveryParam;
import com.integrien.alive.common.adapter3.DiscoveryResult;
import com.integrien.alive.common.adapter3.IdentifierCredentialProperties;
import com.integrien.alive.common.adapter3.Logger;
import com.integrien.alive.common.adapter3.MetricData;
import com.integrien.alive.common.adapter3.MetricKey;
import com.integrien.alive.common.adapter3.Relationships;
import com.integrien.alive.common.adapter3.ResourceKey;
import com.integrien.alive.common.adapter3.ResourceStatus;
import com.integrien.alive.common.adapter3.TestParam;
import com.integrien.alive.common.adapter3.config.ResourceConfig;
import com.integrien.alive.common.adapter3.config.ResourceIdentifierConfig;
import com.integrien.alive.common.adapter3.describe.AdapterDescribe;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Main adapter class for ESightAdapter.
 * 插件的入口类
 *
 * @author harbor
 * @since 2017/9/22
 */
public class ESightAdapter extends AdapterBase {
    // logger instance
    private final Logger logger;

    // ESightAdapterUtil instance
    private final ESightAdapterUtil adapterUtil;

    // Stores metrics for each resource
    private final Map<ResourceKey, List<MetricData>> metricsByResource = new LinkedHashMap<>();

    // Stores the relationships for each resource
    private final Map<ResourceKey, List<ResourceKey>> relationshipsByResource = new LinkedHashMap<>();

    // key map for device name to resource key
    private final Map<String, ResourceKey> deviceResourceKeyMap = new LinkedHashMap<>();

    private final Map<String, String> deviceHealthMap = new LinkedHashMap<>();

    private ESightService service;

    private String host = null;

    /**
     * Default constructor.
     */
    public ESightAdapter() {
        this(null, null);
    }

    /**
     * Parameterized constructor.
     *
     * @param instanceName instance name
     * @param instanceId   instance Id
     */
    public ESightAdapter(String instanceName, Integer instanceId) {
        super(instanceName, instanceId);
        logger = adapterLoggerFactory.getLogger(ESightAdapter.class);
        adapterUtil = new ESightAdapterUtil(adapterLoggerFactory);

        if (logger.isInfoEnabled()) {
            logger.info(
                "Esight adapter instantce created: instanceName = " + instanceName + ", instanceId = " + instanceId);
        }

        service = new ESightServiceImpl();
        service.setLogger(adapterLoggerFactory.getLogger(ESightServiceImpl.class));
    }

    private void setHost(String host) {
        if (this.host == null) {
            this.host = host;
            return;
        }

        if (this.host.equals(host)) {
            return;
        }

        logger.error("eSight Host IP changed from '" + this.host + "' to '" + host + "'.");
        this.host = host;
        metricsByResource.clear();
        relationshipsByResource.clear();
        deviceResourceKeyMap.clear();
    }

    /**
     * 调用eSight REST API采集数据.
     *
     * @param host eSight服务器IP
     * @return ResourceKey列表
     */
    private List<ResourceKey> collectResourceDataFromESight(String host) {
        List<ResourceKey> allResourceList = new ArrayList<>();

        String[] serverTypes = new String[] {
            Constant.TREE_SERVER_TYPE_RACK, Constant.TREE_SERVER_TYPE_BLADE, Constant.TREE_SERVER_TYPE_HIGHDENSITY,
            Constant.TREE_SERVER_TYPE_KUNLUN
        };

        // 服务器类型keyList
        List<ResourceKey> serverTypeKeyList = new ArrayList<>();

        for (String serverType : serverTypes) {
            List<ServerDeviceBean> serverList = service.getServerDeviceList(serverType);
            if (serverList.isEmpty()) {
                continue;
            }

            // 收集 devices key
            List<ResourceKey> serverDeviceKeyList = new ArrayList<>();
            // 服务器分类
            ResourceKey serverTypeKey = new ResourceKey(serverType, Constant.KIND_SERVER_TYPES, getAdapterKind());
            ResourceIdentifierConfig ipIdentifier = new ResourceIdentifierConfig(Constant.ATTR_ID, host + serverType,
                true);
            serverTypeKey.addIdentifier(ipIdentifier);

            List<String> serverTypeChildHealthList = new ArrayList<>();

            allResourceList.add(serverTypeKey);
            serverTypeKeyList.add(serverTypeKey);

            for (ServerDeviceBean deviceBean : serverList) {
                setConfig(host, allResourceList, serverDeviceKeyList, serverTypeChildHealthList, deviceBean);
            }
            // 设置服务器类型(如rack, blade)和server device关联关系
            relationshipsByResource.put(serverTypeKey, serverDeviceKeyList);
            setHealStatus4GroupObject(serverTypeKey, Constant.ATTR_HEALTHSTATE, serverTypeChildHealthList);

            logger.error(serverList.size() + " servers of type = " + serverType + " created.");
        }

        ResourceKey serverGroupKey = new ResourceKey(host, Constant.KIND_HOST_INSTANCE, getAdapterKind());
        long timestamp = System.currentTimeMillis();
        List<MetricData> metricDataList = new ArrayList<>();
        // 设置healthState为正常状态
        metricDataList.add(new MetricData(new MetricKey(false, Constant.ATTR_HEALTHSTATE), timestamp,
            ConvertUtils.convertHealthState(0)));
        metricsByResource.put(serverGroupKey, metricDataList);

        // eSight server IP和serverTypes的对应关系
        relationshipsByResource.put(serverGroupKey, serverTypeKeyList);
        allResourceList.add(serverGroupKey);
        return allResourceList;
    }

    private void setConfig(String host, List<ResourceKey> allResourceList, List<ResourceKey> serverDeviceKeyList,
        List<String> serverTypeChildHealthList, ServerDeviceBean deviceBean) {
        ServerDeviceDetailBean device = service.getServerDetail(deviceBean.getDn());
        if (device == null) {
            logger.error("Failed to get detail of device with dn = " + deviceBean.getDn());
            return;
        }

        ResourceKey deviceResourceKey = device.convert2Resource(deviceBean.getDn(), getAdapterKind(),
            metricsByResource);
        // dn --> device key map
        deviceResourceKeyMap.put(deviceBean.getDn(), deviceResourceKey);
        deviceHealthMap.put(deviceBean.getDn(), ConvertUtils.convertHealthState(device.getStatus()));

        serverTypeChildHealthList.add(ConvertUtils.convertHealthState(device.getStatus()));

        allResourceList.add(deviceResourceKey);
        List<ResourceKey> deviceChildKeys = new ArrayList<>();
        List<ResourceKey> childBladeDeviceKeyList = new ArrayList<>();

        // child Blades
        List<ChildBladeBean> childBladeBeans = deviceBean.getChildBlades();
        for (ChildBladeBean bean : childBladeBeans) {
            childBladeDeviceKeyList.add(deviceResourceKeyMap.get(bean.getDn()));
        }

        // 处理childBlade显示重复的问题
        if (!deviceBean.isChildBlade()) {
            serverDeviceKeyList.add(deviceResourceKey);
        }
        // board
        deviceChildKeys.addAll(setBoard(host, allResourceList, deviceBean, device));

        // CPU
        deviceChildKeys.addAll(setCpu(host, allResourceList, deviceBean, device));

        // disk
        deviceChildKeys.addAll(setDisk(host, allResourceList, deviceBean, device));

        // fan
        deviceChildKeys.addAll(setFan(host, allResourceList, deviceBean, device, childBladeBeans));

        // memory
        deviceChildKeys.addAll(setMemory(host, allResourceList, deviceBean, device));

        // PSU
        deviceChildKeys.addAll(setPsu(host, allResourceList, deviceBean, device, childBladeBeans));

        // device Group
        deviceChildKeys.addAll(setDeviceGroup(device, childBladeDeviceKeyList, childBladeBeans));

        // PCIE
        deviceChildKeys.addAll(setPcie(host, allResourceList, deviceBean, device));

        // RAID
        deviceChildKeys.addAll(setRaid(host, allResourceList, deviceBean, device));

        // network card
        deviceChildKeys.addAll(setNetworkCard(host, allResourceList, deviceBean, device));

        // Mezz
        deviceChildKeys.addAll(setMezz(host, allResourceList, deviceBean, device));
        // 设置server device和group object(如cpuGroup, diskGroup, fanGroup等)的关联关系
        relationshipsByResource.put(deviceResourceKey, deviceChildKeys);
        allResourceList.addAll(deviceChildKeys);
    }

    private List<ResourceKey> setDeviceGroup(ServerDeviceDetailBean device, List<ResourceKey> childBladeDeviceKeyList,
        List<ChildBladeBean> childBladeBeans) {
        List<ResourceKey> deviceChildKeys = new ArrayList<>();
        if (!childBladeDeviceKeyList.isEmpty()) {
            List<String> childHealthList = new ArrayList<>();
            for (ChildBladeBean childBean : childBladeBeans) {
                childHealthList.add(deviceHealthMap.get(childBean.getDn()));
            }
            ResourceKey devicesGroup = device.createGroupKey(Constant.TREE_DEVICES_GROUP, Constant.KIND_DEVICES_GROUP,
                childBladeDeviceKeyList, relationshipsByResource, getAdapterKind());
            deviceChildKeys.add(devicesGroup);
            setHealStatus4GroupObject(devicesGroup, Constant.ATTR_STATUS, childHealthList);
        }
        return deviceChildKeys;
    }

    private List<ResourceKey> setPsu(String host, List<ResourceKey> allResourceList, ServerDeviceBean deviceBean,
        ServerDeviceDetailBean device, List<ChildBladeBean> childBladeBeans) {
        List<ResourceKey> deviceChildKeys = new ArrayList<>();
        List<PSUBean> psuBeans = device.getPSU();
        if (!psuBeans.isEmpty()) {
            List<ResourceKey> psuResourceKey = new ArrayList<>();
            List<String> childHealthList = new ArrayList<>();
            for (PSUBean psu : psuBeans) {
                ResourceKey key = psu.convert2Resource(host + deviceBean.getDn(), getAdapterKind(), metricsByResource);
                psuResourceKey.add(key);
                allResourceList.add(key);
                if (childBladeBeans.isEmpty()) {
                    childHealthList.add(ConvertUtils.convertHealthState(psu.getHealthState()));
                }
            }
            // PSU Group
            ResourceKey psuGroup = device.createGroupKey(Constant.TREE_PSU_GROUP, Constant.KIND_PSU_GROUP,
                psuResourceKey, relationshipsByResource, getAdapterKind());
            deviceChildKeys.add(psuGroup);
            // add health state here
            if (!childBladeBeans.isEmpty()) {
                String groupHealth = ConvertUtils.convertHealthState(device.getPemHealth());
                childHealthList.add(groupHealth);
            }
            setHealStatus4GroupObject(psuGroup, Constant.ATTR_HEALTHSTATE, childHealthList);
        }
        return deviceChildKeys;
    }

    private List<ResourceKey> setMezz(String host, List<ResourceKey> allResourceList, ServerDeviceBean deviceBean,
        ServerDeviceDetailBean device) {
        List<ResourceKey> deviceChildKeys = new ArrayList<>();
        List<MezzBean> mezzBeans = device.getMezz();
        if (!mezzBeans.isEmpty()) {
            List<ResourceKey> mezzResourceKey = new ArrayList<>();
            List<String> childHealthList = new ArrayList<>();
            for (MezzBean bean : mezzBeans) {
                ResourceKey key = bean.convert2Resource(host + deviceBean.getDn(), getAdapterKind(), metricsByResource);
                mezzResourceKey.add(key);
                allResourceList.add(key);
                childHealthList.add(ConvertUtils.convertHealthState(bean.getMezzHealthStatus()));
            }
            // Mezz Group
            ResourceKey mezzGroup = device.createGroupKey(Constant.TREE_MEZZ_GROUP, Constant.KIND_MEZZ_GROUP,
                mezzResourceKey, relationshipsByResource, getAdapterKind());
            deviceChildKeys.add(mezzGroup);
            // add health state here
            setHealStatus4GroupObject(mezzGroup, Constant.ATTR_MEZZ_HEALTH_STATUS, childHealthList);
        }
        return deviceChildKeys;
    }

    private List<ResourceKey> setNetworkCard(String host, List<ResourceKey> allResourceList,
        ServerDeviceBean deviceBean, ServerDeviceDetailBean device) {
        List<ResourceKey> deviceChildKeys = new ArrayList<>();
        List<NetworkCardBean> networkcardBeans = device.getNetworkCard();
        if (!networkcardBeans.isEmpty()) {
            List<ResourceKey> netWorkCardResourceKey = new ArrayList<>();
            List<String> childHealthList = new ArrayList<>();
            for (NetworkCardBean bean : networkcardBeans) {
                ResourceKey key = bean.convert2Resource(host + deviceBean.getDn(), getAdapterKind(), metricsByResource);
                netWorkCardResourceKey.add(key);
                allResourceList.add(key);
                childHealthList.add(ConvertUtils.convertHealthState(bean.getHealthState()));
            }
            // network card Group
            ResourceKey networkCardGroup = device.createGroupKey(Constant.TREE_NETWORK_CARD_GROUP,
                Constant.KIND_NETWORK_CARD_GROUP, netWorkCardResourceKey, relationshipsByResource, getAdapterKind());
            deviceChildKeys.add(networkCardGroup);
            setHealStatus4GroupObject(networkCardGroup, Constant.ATTR_HEALTHSTATE, childHealthList);
        }
        return deviceChildKeys;
    }

    private List<ResourceKey> setRaid(String host, List<ResourceKey> allResourceList, ServerDeviceBean deviceBean,
        ServerDeviceDetailBean device) {
        List<ResourceKey> deviceChildKeys = new ArrayList<>();
        List<RAIDBean> raidBeans = device.getRAID();
        if (!raidBeans.isEmpty()) {
            List<ResourceKey> raidResourceKey = new ArrayList<>();
            List<String> childHealthList = new ArrayList<>();
            for (RAIDBean raid : raidBeans) {
                ResourceKey key = raid.convert2Resource(host + deviceBean.getDn(), getAdapterKind(), metricsByResource);
                raidResourceKey.add(key);
                allResourceList.add(key);
                childHealthList.add(ConvertUtils.convertHealthState(raid.getHealthState()));
            }
            // RAID Group
            ResourceKey raidGroup = device.createGroupKey(Constant.TREE_RAID_GROUP, Constant.KIND_RAID_GROUP,
                raidResourceKey, relationshipsByResource, getAdapterKind());
            deviceChildKeys.add(raidGroup);
            // add health state here
            setHealStatus4GroupObject(raidGroup, Constant.ATTR_HEALTHSTATE, childHealthList);
        }
        return deviceChildKeys;
    }

    private List<ResourceKey> setPcie(String host, List<ResourceKey> allResourceList, ServerDeviceBean deviceBean,
        ServerDeviceDetailBean device) {
        List<ResourceKey> deviceChildKeys = new ArrayList<>();
        List<PCIEBean> pcieBeans = device.getPCIE();
        if (!pcieBeans.isEmpty()) {
            List<ResourceKey> pcieResourceKey = new ArrayList<>();
            List<String> childHealthList = new ArrayList<>();
            for (PCIEBean pcie : pcieBeans) {
                ResourceKey key = pcie.convert2Resource(host + deviceBean.getDn(), getAdapterKind(), metricsByResource);
                pcieResourceKey.add(key);
                allResourceList.add(key);
                childHealthList.add(ConvertUtils.convertHealthState(pcie.getPcieSsdCardHealthStatus()));
            }
            // PCIE Group
            ResourceKey pcieGroup = device.createGroupKey(Constant.TREE_PCIE_GROUP, Constant.KIND_PCIE_GROUP,
                pcieResourceKey, relationshipsByResource, getAdapterKind());
            deviceChildKeys.add(pcieGroup);
            // add health state here
            setHealStatus4GroupObject(pcieGroup, Constant.ATTR_PCIESSDCARD_HEALTHSTATUS, childHealthList);
        }
        return deviceChildKeys;
    }

    private List<ResourceKey> setMemory(String host, List<ResourceKey> allResourceList, ServerDeviceBean deviceBean,
        ServerDeviceDetailBean device) {
        List<ResourceKey> deviceChildKeys = new ArrayList<>();
        List<MemoryBean> memoryBeans = device.getMemory();
        if (!memoryBeans.isEmpty()) {
            List<ResourceKey> memoryResourceKey = new ArrayList<>();
            List<String> childHealthList = new ArrayList<>();
            for (MemoryBean memory : memoryBeans) {
                ResourceKey key = memory.convert2Resource(host + deviceBean.getDn(), getAdapterKind(),
                    metricsByResource);
                memoryResourceKey.add(key);
                allResourceList.add(key);
                childHealthList.add(ConvertUtils.convertHealthState(memory.getHealthState()));
            }
            // memory Group
            ResourceKey memoryGroup = device.createGroupKey(Constant.TREE_MEMORY_GROUP, Constant.KIND_MEMORY_GROUP,
                memoryResourceKey, relationshipsByResource, getAdapterKind());
            deviceChildKeys.add(memoryGroup);
            // add health state here
            setHealStatus4GroupObject(memoryGroup, Constant.ATTR_HEALTHSTATE, childHealthList);
        }
        return deviceChildKeys;
    }

    private List<ResourceKey> setFan(String host, List<ResourceKey> allResourceList, ServerDeviceBean deviceBean,
        ServerDeviceDetailBean device, List<ChildBladeBean> childBladeBeans) {
        List<ResourceKey> deviceChildKeys = new ArrayList<>();
        List<FanBean> fanBeans = device.getFan();
        if (!fanBeans.isEmpty()) {
            List<ResourceKey> fanResourceKey = new ArrayList<>();
            List<String> childHealthList = new ArrayList<>();
            for (FanBean fan : fanBeans) {
                ResourceKey key = fan.convert2Resource(host + deviceBean.getDn(), getAdapterKind(), metricsByResource);
                fanResourceKey.add(key);
                allResourceList.add(key);
                if (childBladeBeans.isEmpty()) {
                    childHealthList.add(ConvertUtils.convertHealthState(fan.getHealthState()));
                }
            }
            // fan Group
            ResourceKey fanGroup = device.createGroupKey(Constant.TREE_FAN_GROUP, Constant.KIND_FAN_GROUP,
                fanResourceKey, relationshipsByResource, getAdapterKind());
            deviceChildKeys.add(fanGroup);
            // add health state here
            if (!childBladeBeans.isEmpty()) {
                String groupHealth = ConvertUtils.convertHealthState(device.getFanOverHealth());
                childHealthList.add(groupHealth);
            }
            setHealStatus4GroupObject(fanGroup, Constant.ATTR_HEALTHSTATE, childHealthList);
        }
        return deviceChildKeys;
    }

    private List<ResourceKey> setDisk(String host, List<ResourceKey> allResourceList, ServerDeviceBean deviceBean,
        ServerDeviceDetailBean device) {
        List<ResourceKey> deviceChildKeys = new ArrayList<>();
        List<DiskBean> diskBeans = device.getDisk();
        if (!diskBeans.isEmpty()) {
            List<ResourceKey> diskResourceKey = new ArrayList<>();
            List<String> childHealthList = new ArrayList<>();
            for (DiskBean disk : diskBeans) {
                ResourceKey key = disk.convert2Resource(host + deviceBean.getDn(), getAdapterKind(), metricsByResource);
                diskResourceKey.add(key);
                allResourceList.add(key);
                childHealthList.add(ConvertUtils.convertHealthState(disk.getHealthState()));
            }
            // disk Group
            ResourceKey diskGroup = device.createGroupKey(Constant.TREE_DISK_GROUP, Constant.KIND_DISK_GROUP,
                diskResourceKey, relationshipsByResource, getAdapterKind());
            deviceChildKeys.add(diskGroup);
            // add health state here
            setHealStatus4GroupObject(diskGroup, Constant.ATTR_HEALTHSTATE, childHealthList);
        }
        return deviceChildKeys;
    }

    private List<ResourceKey> setCpu(String host, List<ResourceKey> allResourceList, ServerDeviceBean deviceBean,
        ServerDeviceDetailBean device) {
        List<ResourceKey> deviceChildKeys = new ArrayList<>();
        List<CPUBean> cpuBeans = device.getCPU();
        if (!cpuBeans.isEmpty()) {
            List<ResourceKey> cpuResourceKey = new ArrayList<>();
            List<String> childHealthList = new ArrayList<>();
            for (CPUBean cpu : cpuBeans) {
                ResourceKey key = cpu.convert2Resource(host + deviceBean.getDn(), getAdapterKind(), metricsByResource);
                cpuResourceKey.add(key);
                allResourceList.add(key);
                childHealthList.add(ConvertUtils.convertHealthState(cpu.getHealthState()));
            }
            // CPU Group
            ResourceKey cpuGroup = device.createGroupKey(Constant.TREE_CPU_GROUP, Constant.KIND_CPU_GROUP,
                cpuResourceKey, relationshipsByResource, getAdapterKind());
            deviceChildKeys.add(cpuGroup);
            // add health state here
            setHealStatus4GroupObject(cpuGroup, Constant.ATTR_HEALTHSTATE, childHealthList);
        }
        return deviceChildKeys;
    }

    private List<ResourceKey> setBoard(String host, List<ResourceKey> allResourceList, ServerDeviceBean deviceBean,
        ServerDeviceDetailBean device) {
        List<ResourceKey> deviceChildKeys = new ArrayList<>();
        List<BoardBean> boardBeans = device.getBoard();
        if (!boardBeans.isEmpty()) {
            List<ResourceKey> boardResourceKey = new ArrayList<>();
            List<String> childHealthList = new ArrayList<>();
            for (BoardBean board : boardBeans) {
                ResourceKey boardKey = board.convert2Resource(host + deviceBean.getDn(), getAdapterKind(),
                    metricsByResource);
                boardResourceKey.add(boardKey);
                allResourceList.add(boardKey);
                childHealthList.add(ConvertUtils.convertHealthState(board.getHealthState()));
            }
            // board Group
            ResourceKey boardGroup = device.createGroupKey(Constant.TREE_BOARD_GROUP, Constant.KIND_BOARD_GROUP,
                boardResourceKey, relationshipsByResource, getAdapterKind());
            deviceChildKeys.add(boardGroup);
            // add health state here
            setHealStatus4GroupObject(boardGroup, Constant.ATTR_HEALTHSTATE, childHealthList);
        }
        return deviceChildKeys;
    }

    /**
     * The responsibility of the method is to provide adapter describe
     * information to the collection framework.
     *
     * @return AdapterDescribe
     */
    @Override
    public AdapterDescribe onDescribe() {
        logger.info("Inside onDescribe method of ESightAdapter class");
        return adapterUtil.createAdapterDescribe();
    }

    /**
     * This method is called when user wants to discover resources in the target
     * system manually. onConfigure is not called before onDiscover.
     *
     * @param discParam DiscoveryParam
     * @return DiscoveryResult
     */
    @Override
    public DiscoveryResult onDiscover(DiscoveryParam discParam) {
        logger.info("Inside onDiscover method of ESightAdapter class");
        DiscoveryResult discoveryResult = new DiscoveryResult(discParam.getAdapterInstResource());
        return discoveryResult;
    }

    /**
     * This method is called for each collection cycle. By default, this value
     * is 5 minutes unless user changes it
     *
     * @param adapterInstResource ResourceConfig
     * @param monitoringResources Collection<ResourceConfig>
     */
    @Override
    public void onCollect(ResourceConfig adapterInstResource, Collection<ResourceConfig> monitoringResources) {
        if (logger.isInfoEnabled()) {
            logger.info("Inside onCollect method of ESightAdapter class");
        }

        final IdentifierCredentialProperties prop = new IdentifierCredentialProperties(adapterLoggerFactory,
            adapterInstResource);

        String hostIp = prop.getIdentifier(Constant.KEY_SERVER_IP_ADDRESS, "").trim();
        setHost(hostIp);

        int hostPort = prop.getIntIdentifier(Constant.KEY_ESIGHT_SERVER_PORT, Constant.DEFAULT_ESIGHT_SERVER_PORT);
        String username = prop.getCredential(Constant.KEY_ESIGHT_ACCOUNT);
        String eSightCode = prop.getCredential(Constant.KEY_ESIGHT_CODE);

        String openid = null;
        try {
            openid = service.login(hostIp, hostPort, username, eSightCode);
        } catch (EsightException e) {
            logger.error(e.getMessage() + ": eSight server (" + hostIp + ") authentication failed.", e);
        }
        if (openid == null || openid.isEmpty()) {
            configEsight(hostIp);
            return;
        }

        final Long startTime = System.nanoTime();

        DiscoveryResult discoveryResult = collectResult.getDiscoveryResult(true);
        List<ResourceKey> resources = collectResourceDataFromESight(hostIp);

        // 注销会话 openid
        service.logout(openid);

        if (resources.size() == 0) {
            logger.error("No resources collected from server with IP " + hostIp);
        } else {
            logger.error(resources.size() + " resources collected from server with IP " + hostIp);
        }

        for (ResourceKey resourceKey : resources) {
            useResourceKey(discoveryResult, resourceKey);
        }

        Long seconds = TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        logger.error("Collected resource from esight elapsed time is " + seconds + " seconds.");

        if (seconds > Constant.DEFAULT_COLLECT_INTERVAL * 60) {
            logger.error("PLEASE UPDATE THE COLLECT INTERVAL GREATER THAN " + seconds + " SECONDS.");
        }
    }

    private void configEsight(String hostIp) {
        ResourceKey resourceKey = new ResourceKey(hostIp, Constant.KIND_HOST_INSTANCE, getAdapterKind());
        long timestamp = System.currentTimeMillis();
        List<MetricData> metricDataList = new ArrayList<>();
        // 设置eSight服务器的状态为离线状态
        metricDataList.add(new MetricData(new MetricKey(false, Constant.ATTR_HEALTHSTATE), timestamp,
            ConvertUtils.convertHealthState(-1)));
        metricsByResource.put(resourceKey, metricDataList);
        DiscoveryResult discoveryResult = collectResult.getDiscoveryResult(true);
        if (isNewResource(resourceKey)) {
            discoveryResult.addResource(new ResourceConfig(resourceKey));
        }

        // Check if resource is part of monitored set, otherwise continue
        ResourceConfig resourceConfig = getMonitoringResource(resourceKey);
        if (resourceConfig == null) {
            return;
        }

        // Add metrics
        addMetricData(resourceConfig, metricsByResource.get(resourceKey));
        // Add relationships
        Relationships rel = new Relationships();
        rel.setRelationships(resourceKey, relationshipsByResource.get(resourceKey),
            Collections.singleton(getAdapterKind()));
        discoveryResult.addRelationships(rel);
        return;
    }

    private void useResourceKey(DiscoveryResult discoveryResult, ResourceKey resourceKey) {
        if (isNewResource(resourceKey)) {
            discoveryResult.addResource(new ResourceConfig(resourceKey));
        }

        // Check if resource is part of monitored set, otherwise continue
        ResourceConfig resourceConfig = getMonitoringResource(resourceKey);
        if (resourceConfig == null) {
            return;
        }

        // Add metrics
        addMetricData(resourceConfig, metricsByResource.get(resourceKey));

        // Add relationships
        Relationships rel = new Relationships();
        rel.setRelationships(resourceKey, relationshipsByResource.get(resourceKey),
            Collections.singleton(getAdapterKind()));
        discoveryResult.addRelationships(rel);
    }

    /**
     * This method is called when a new adapter instance is created.
     *
     * @param resStatus           ResourceStatus
     * @param adapterInstResource ResourceConfig
     */
    @Override
    public void onConfigure(ResourceStatus resStatus, ResourceConfig adapterInstResource) {
        final IdentifierCredentialProperties prop = new IdentifierCredentialProperties(adapterLoggerFactory,
            adapterInstResource);
        String hostIp = prop.getIdentifier(Constant.KEY_SERVER_IP_ADDRESS, "").trim();
        logger.error("A new adapter instance with server ip '" + hostIp + "' is created");
    }

    /**
     * This method is called when presses "Test" button while
     * creating/editing an adapter instance.
     *
     * @param testParam TestParam
     * @return boolean
     */
    @Override
    public boolean onTest(TestParam testParam) {
        ResourceConfig adapterInstanceResource = testParam.getAdapterConfig().getAdapterInstResource();
        final IdentifierCredentialProperties prop = new IdentifierCredentialProperties(adapterLoggerFactory,
            adapterInstanceResource);

        if (logger.isInfoEnabled()) {
            logger.info("Inside onTest method of ESightAdapter class");
        }

        String empty = "";
        String hostIp = prop.getIdentifier(Constant.KEY_SERVER_IP_ADDRESS, empty).trim();
        int serverPort = prop.getIntIdentifier(Constant.KEY_ESIGHT_SERVER_PORT, Constant.DEFAULT_ESIGHT_SERVER_PORT);
        String username = prop.getCredential(Constant.KEY_ESIGHT_ACCOUNT);
        String password = prop.getCredential(Constant.KEY_ESIGHT_CODE);

        try {
            if (!isValidUrl(hostIp, serverPort)) {
                throw new EsightException("invalid host ip");
            }
            ESightService serviceImpl = new ESightServiceImpl();
            String openid = serviceImpl.login(hostIp, serverPort, username, password);

            if (openid == null || openid.isEmpty()) {
                return false;
            }
            serviceImpl.logout(openid);
        } catch (EsightException e) {
            logger.error("Test eSight login failed.", e);
            return false;
        }

        return true;
    }

    private boolean isValidUrl(String host, int port) {
        String urlStr = "https://" + host + ":" + port;
        URL url;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            logger.error("invalid url");
            return false;
        }
        return !url.getHost().equalsIgnoreCase("localhost") && !url.getHost().startsWith("127");
    }

    /**
     * 设置健康状态
     *
     * @param resourceKey     ResourceKey
     * @param attrName        String
     * @param childStatusList Optional values are {OK, Unknown, Warning, Immediate, Critical}
     *                        Level from High to low is:  OK, Unknown, Warning, Immediate, Critical
     */
    private void setHealStatus4GroupObject(ResourceKey resourceKey, String attrName, List<String> childStatusList) {
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

        if ("Unknown".equals(defaultStatus)) {
            defaultStatus = "Warning";
        }

        List<MetricData> metricDataList = new ArrayList<>();
        long timestamp = System.currentTimeMillis();
        metricDataList.add(new MetricData(new MetricKey(false, attrName), timestamp, defaultStatus));
        metricsByResource.put(resourceKey, metricDataList);
    }
}
