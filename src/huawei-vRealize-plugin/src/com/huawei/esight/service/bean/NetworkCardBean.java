package com.huawei.esight.service.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.huawei.adapter.util.ConvertUtils;
import com.integrien.alive.common.adapter3.MetricData;
import com.integrien.alive.common.adapter3.MetricKey;
import com.integrien.alive.common.adapter3.ResourceKey;
import com.integrien.alive.common.adapter3.config.ResourceIdentifierConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * NetworkCard实体类.
 * Created by harbor on 7/18/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkCardBean implements TreeNodeResource {
    
	private String moId;
	
	private String uuid;
	
	private String deviceName;
	
	private String cardName;
	
	private String cardManufacturer;
	
	private String cardModel;
	
	private String chipManufacturer;
	
	private String chipModel;
	
	private String firmwarePkgVersion;
	
	private String driverName;
	
	private String driverVersion;
	
	private int healthState = -2;
    
    public String getCardModel() {
		return cardModel;
	}



	public void setCardModel(String cardModel) {
		this.cardModel = cardModel;
	}



	public String getMoId() {
		return moId;
	}



	public void setMoId(String moId) {
		this.moId = moId;
	}



	public String getUuid() {
		return uuid;
	}



	public void setUuid(String uuid) {
		this.uuid = uuid;
	}



	public String getDeviceName() {
		return deviceName;
	}



	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}



	public String getCardName() {
		return cardName;
	}



	public void setCardName(String cardName) {
		this.cardName = cardName;
	}



	public String getCardManufacturer() {
		return cardManufacturer;
	}



	public void setCardManufacturer(String cardManufacturer) {
		this.cardManufacturer = cardManufacturer;
	}



	public String getChipManufacturer() {
		return chipManufacturer;
	}



	public void setChipManufacturer(String chipManufacturer) {
		this.chipManufacturer = chipManufacturer;
	}



	public String getChipModel() {
		return chipModel;
	}



	public void setChipModel(String chipModel) {
		this.chipModel = chipModel;
	}



	public String getFirmwarePkgVersion() {
		return firmwarePkgVersion;
	}



	public void setFirmwarePkgVersion(String firmwarePkgVersion) {
		this.firmwarePkgVersion = firmwarePkgVersion;
	}



	public String getDriverName() {
		return driverName;
	}



	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}



	public String getDriverVersion() {
		return driverVersion;
	}



	public void setDriverVersion(String driverVersion) {
		this.driverVersion = driverVersion;
	}



	public int getHealthState() {
		return healthState;
	}



	public void setHealthState(int healthState) {
		this.healthState = healthState;
	}



	@Override
    public ResourceKey convert2Resource(String dn, String adapterKind,
            Map<ResourceKey, List<MetricData>> metricsByResource) {
        
        ResourceKey resourceKey = 
                new ResourceKey(this.deviceName, Constant.KIND_NETWORKCATD, adapterKind);
        //设定唯一的标识，保证同名的资源可以正常显示
        ResourceIdentifierConfig dnIdentifier = 
                new ResourceIdentifierConfig(Constant.ATTR_ID, dn + this.uuid, true);
        resourceKey.addIdentifier(dnIdentifier);
        long timestamp = System.currentTimeMillis();
        
        List<MetricData> metricDataList = new ArrayList<>();
        
        //写入resource属性
        
        metricDataList.add(
                new MetricData(new MetricKey(true).add(Constant.ATTR_UUID), timestamp, this.uuid));
        
        metricDataList.add(new MetricData(new MetricKey(true, "deviceName"), timestamp, this.deviceName));
        metricDataList.add(new MetricData(new MetricKey(true, "cardName"), timestamp, this.cardName));
        metricDataList.add(new MetricData(new MetricKey(true, "cardManufacturer"), timestamp, this.cardManufacturer));
        metricDataList.add(new MetricData(new MetricKey(true, "cardModel"), timestamp, this.cardModel));
        metricDataList.add(new MetricData(new MetricKey(true, "chipManufacturer"), timestamp, this.chipManufacturer));
        metricDataList.add(new MetricData(new MetricKey(true, "chipModel"), timestamp, this.chipModel));
        metricDataList.add(new MetricData(new MetricKey(true, "firmwarePkgVersion"), timestamp, this.firmwarePkgVersion));
        metricDataList.add(new MetricData(new MetricKey(true, "driverName"), timestamp, this.driverName));
        metricDataList.add(new MetricData(new MetricKey(true, "driverVersion"), timestamp, this.driverVersion));
        
        metricDataList.add(
                new MetricData(new MetricKey(false).add(Constant.ATTR_HEALTHSTATE), timestamp, 
                        ConvertUtils.convertHealthState(this.healthState)));
        
        //关联key和属性值
        metricsByResource.put(resourceKey, metricDataList);
        return resourceKey;
    }

}
