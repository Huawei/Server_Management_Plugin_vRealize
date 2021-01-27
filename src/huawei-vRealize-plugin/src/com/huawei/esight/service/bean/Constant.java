/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 */

package com.huawei.esight.service.bean;

/**
 * 全局常量类.
 *
 * @author jiawei
 * @since 2017/9/22
 */
public class Constant {
    /**
     * KEY_SERVER_IP_ADDRESS
     */
    public static final String KEY_SERVER_IP_ADDRESS = "serverIP";

    /**
     * KEY_ESIGHT_SERVER_PORT
     */
    public static final String KEY_ESIGHT_SERVER_PORT = "serverPort";

    /**
     * DEFAULT_ESIGHT_SERVER_PORT
     */
    public static final int DEFAULT_ESIGHT_SERVER_PORT = 32102;

    /**
     * KEY_ESIGHT_ACCOUNT
     */
    public static final String KEY_ESIGHT_ACCOUNT = "username";

    /**
     * KEY_ESIGHT_CODE
     */
    public static final String KEY_ESIGHT_CODE = "esightCode";

    /**
     * PAGE_SIZE
     */
    public static final String PAGE_SIZE = "100";

    /**
     * DEFAULT_COLLECT_INTERVAL : default collect interval in minutes
     */
    public static final int DEFAULT_COLLECT_INTERVAL = 10;

    /**
     * TREE_SERVER_TYPE_RACK
     */
    public static final String TREE_SERVER_TYPE_RACK = "rack";

    /**
     * TREE_SERVER_TYPE_BLADE
     */
    public static final String TREE_SERVER_TYPE_BLADE = "blade";

    /**
     * TREE_SERVER_TYPE_HIGHDENSITY
     */
    public static final String TREE_SERVER_TYPE_HIGHDENSITY = "highdensity";

    /**
     * TREE_SERVER_TYPE_KUNLUN
     */
    public static final String TREE_SERVER_TYPE_KUNLUN = "kunlun";

    /**
     * KIND_SERVER_TYPES
     */
    public static final String KIND_SERVER_TYPES = "serverTypes";

    /**
     * ATTR_ID
     */
    public static final String ATTR_ID = "id";

    /**
     * KIND_HOST_INSTANCE
     */
    public static final String KIND_HOST_INSTANCE = "hostInstance";

    /**
     * KIND_BOARD
     */
    public static final String KIND_BOARD = "board";

    /**
     * KIND_CPU
     */
    public static final String KIND_CPU = "cpu";

    /**
     * KIND_DISK
     */
    public static final String KIND_DISK = "disk";

    /**
     * KIND_FAN
     */
    public static final String KIND_FAN = "fan";

    /**
     * KIND_MEMORY
     */
    public static final String KIND_MEMORY = "memory";

    /**
     * KIND_NETWORKCATD
     */
    public static final String KIND_NETWORKCATD = "card";

    /**
     * KIND_PCIE
     */
    public static final String KIND_PCIE = "pcie";

    /**
     * KIND_PSU
     */
    public static final String KIND_PSU = "psu";

    /**
     * KIND_RAID
     */
    public static final String KIND_RAID = "raid";

    /**
     * KIND_SERVER_DEVICE
     */
    public static final String KIND_SERVER_DEVICE = "serverDevice";

    /**
     * KIND_MEZZ
     */
    public static final String KIND_MEZZ = "mezz";

    /**
     * ATTR_NAME : 硬件属性
     */
    public static final String ATTR_NAME = "name";

    /**
     * ATTR_NETWORKCARD_NAME
     */
    public static final String ATTR_NETWORKCARD_NAME = "netWorkCardName";

    /**
     * ATTR_TYPE
     */
    public static final String ATTR_TYPE = "type";

    /**
     * ATTR_SN
     */
    public static final String ATTR_SN = "sn";

    /**
     * ATTR_PART_NUMBER
     */
    public static final String ATTR_PART_NUMBER = "partNumber";

    /**
     * ATTR_MANUFACTURE
     */
    public static final String ATTR_MANUFACTURE = "manufacture";

    /**
     * ATTR_MANUTIME
     */
    public static final String ATTR_MANUTIME = "manuTime";

    /**
     * ATTR_UUID
     */
    public static final String ATTR_UUID = "uuid";

    /**
     * ATTR_MOID
     */
    public static final String ATTR_MOID = "moId";

    /**
     * ATTR_HEALTHSTATE
     */
    public static final String ATTR_HEALTHSTATE = "healthState";

    /**
     * ATTR_PRESENTSTATE
     */
    public static final String ATTR_PRESENTSTATE = "presentState";

    /**
     * ATTR_FREQUENCY
     */
    public static final String ATTR_FREQUENCY = "frequency";

    /**
     * ATTR_MODEL
     */
    public static final String ATTR_MODEL = "model";

    /**
     * ATTR_LOCATION
     */
    public static final String ATTR_LOCATION = "location";

    /**
     * ATTR_ROTATE
     */
    public static final String ATTR_ROTATE = "rotate";

    /**
     * ATTR_ROTATEPERCENT
     */
    public static final String ATTR_ROTATEPERCENT = "rotatePercent";

    /**
     * ATTR_CONTROLMODEL
     */
    public static final String ATTR_CONTROLMODEL = "controlModel";

    /**
     * ATTR_CAPACITY
     */
    public static final String ATTR_CAPACITY = "capacity";

    /**
     * ATTR_MACADRESS
     */
    public static final String ATTR_MACADRESS = "macAdress";

    /**
     * ATTR_PCIESSDCARD_HEALTHSTATUS
     */
    public static final String ATTR_PCIESSDCARD_HEALTHSTATUS = "pcieSsdCardHealthStatus";

    /**
     * ATTR_PCIESSDCARD_LIFELEFT
     */
    public static final String ATTR_PCIESSDCARD_LIFELEFT = "pcieSsdCardLifeLeft";

    /**
     * ATTR_PCIESSDCARD_MANYFACTURER
     */
    public static final String ATTR_PCIESSDCARD_MANYFACTURER = "pciecardManufacturer";

    /**
     * ATTR_INPUTPOWER
     */
    public static final String ATTR_INPUTPOWER = "inputPower";

    /**
     * ATTR_VERSION
     */
    public static final String ATTR_VERSION = "version";

    /**
     * ATTR_INPUTMODE
     */
    public static final String ATTR_INPUTMODE = "inputMode";

    /**
     * ATTR_POWER_PROTOCOL
     */
    public static final String ATTR_POWER_PROTOCOL = "powerProtocol";

    /**
     * ATTR_RATE_POWER
     */
    public static final String ATTR_RATE_POWER = "ratePower";

    /**
     * ATTR_RAID_TYPE
     */
    public static final String ATTR_RAID_TYPE = "raidType";

    /**
     * ATTR_INTERFACE_TYPE
     */
    public static final String ATTR_INTERFACE_TYPE = "interfaceType";

    /**
     * ATTR_BBU_TYPE
     */
    public static final String ATTR_BBU_TYPE = "bbuType";

    /**
     * ATTR_MEZZ_HEALTH_STATUS
     */
    public static final String ATTR_MEZZ_HEALTH_STATUS = "mezzHealthStatus";

    /**
     * ATTR_MEZZ_INFO
     */
    public static final String ATTR_MEZZ_INFO = "mezzInfo";

    /**
     * ATTR_MEZZ_LOCATION
     */
    public static final String ATTR_MEZZ_LOCATION = "mezzLocation";

    /**
     * ATTR_MEZZ_MAC
     */
    @Deprecated
    public static final String ATTR_MEZZ_MAC = "mezzMac";

    /**
     * ATTR_MEZZ_ETH_MAC
     */
    public static final String ATTR_MEZZ_ETH_MAC = "mezzETHMac";

    /**
     * ATTR_MEZZ_IB_MAC
     */
    public static final String ATTR_MEZZ_IB_MAC = "mezzIBMac";

    /**
     * ATTR_MEZZ_FCWWPN
     */
    public static final String ATTR_MEZZ_FCWWPN = "mezzFCWWPN";

    /**
     * ATTR_PORT_NAME
     */
    public static final String ATTR_PORT_NAME = "portName";

    /**
     * ATTR_LINK_STATUS
     */
    public static final String ATTR_LINK_STATUS = "linkStatus";

    /**
     * ATTR_DN
     */
    public static final String ATTR_DN = "dn";

    /**
     * ATTR_IP_ADDRESS
     */
    public static final String ATTR_IP_ADDRESS = "ipAddress";

    /**
     * ATTR_STATUS
     */
    public static final String ATTR_STATUS = "status";

    /**
     * ATTR_DESC
     */
    public static final String ATTR_DESC = "desc";

    /**
     * ATTR_CPU_NUMS
     */
    public static final String ATTR_CPU_NUMS = "cpuNums";

    /**
     * ATTR_CPU_CORES
     */
    public static final String ATTR_CPU_CORES = "cpuCores";

    /**
     * ATTR_MEMORY_CAPACITY
     */
    public static final String ATTR_MEMORY_CAPACITY = "memoryCapacity";

    /**
     * ATTR_PRODUCT_SN
     */
    public static final String ATTR_PRODUCT_SN = "productSn";

    /**
     * ATTR_BMC_MAC_ADDR
     */
    public static final String ATTR_BMC_MAC_ADDR = "bmcMacAddr";

    /**
     * TREE_BOARD_GROUP
     */
    public static final String TREE_BOARD_GROUP = "BoardGroup";

    /**
     * KIND_BOARD_GROUP
     */
    public static final String KIND_BOARD_GROUP = "boardGroup";

    /**
     * TREE_CPU_GROUP
     */
    public static final String TREE_CPU_GROUP = "CPUGroup";

    /**
     * KIND_CPU_GROUP
     */
    public static final String KIND_CPU_GROUP = "cpuGroup";

    /**
     * TREE_DISK_GROUP
     */
    public static final String TREE_DISK_GROUP = "DiskGroup";

    /**
     * KIND_DISK_GROUP
     */
    public static final String KIND_DISK_GROUP = "diskGroup";

    /**
     * TREE_FAN_GROUP
     */
    public static final String TREE_FAN_GROUP = "FanGroup";

    /**
     * KIND_FAN_GROUP
     */
    public static final String KIND_FAN_GROUP = "fanGroup";

    /**
     * TREE_MEMORY_GROUP
     */
    public static final String TREE_MEMORY_GROUP = "MemoryGroup";

    /**
     * KIND_MEMORY_GROUP
     */
    public static final String KIND_MEMORY_GROUP = "memoryGroup";

    /**
     * TREE_PSU_GROUP
     */
    public static final String TREE_PSU_GROUP = "PSUGroup";

    /**
     * KIND_PSU_GROUP
     */
    public static final String KIND_PSU_GROUP = "psuGroup";

    /**
     * TREE_DEVICES_GROUP
     */
    public static final String TREE_DEVICES_GROUP = "DevicesGroup";

    /**
     * KIND_DEVICES_GROUP
     */
    public static final String KIND_DEVICES_GROUP = "devicesGroup";

    /**
     * TREE_PCIE_GROUP
     */
    public static final String TREE_PCIE_GROUP = "PCIeCardGroup";

    /**
     * KIND_PCIE_GROUP
     */
    public static final String KIND_PCIE_GROUP = "pcieGroup";

    /**
     * TREE_RAID_GROUP
     */
    public static final String TREE_RAID_GROUP = "RAIDCardGroup";

    /**
     * KIND_RAID_GROUP
     */
    public static final String KIND_RAID_GROUP = "raidGroup";

    /**
     * TREE_NETWORK_CARD_GROUP
     */
    public static final String TREE_NETWORK_CARD_GROUP = "NetworkCardGroup";

    /**
     * KIND_NETWORK_CARD_GROUP
     */
    public static final String KIND_NETWORK_CARD_GROUP = "cardGroup";

    /**
     * TREE_MEZZ_GROUP
     */
    public static final String TREE_MEZZ_GROUP = "MEZZCardGroup";

    /**
     * KIND_MEZZ_GROUP
     */
    public static final String KIND_MEZZ_GROUP = "mezzGroup";
}
