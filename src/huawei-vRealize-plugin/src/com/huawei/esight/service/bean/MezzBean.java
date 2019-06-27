package com.huawei.esight.service.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.huawei.adapter.util.ConvertUtils;
import com.integrien.alive.common.adapter3.MetricData;
import com.integrien.alive.common.adapter3.MetricKey;
import com.integrien.alive.common.adapter3.ResourceKey;
import com.integrien.alive.common.adapter3.config.ResourceIdentifierConfig;

public class MezzBean implements TreeNodeResource {
    
    private String name;
    
    private int mezzHealthStatus;
    
    private int presentState;
    
    private String mezzInfo;
    
    private String mezzLocation;
    
    private String mezzMac;
    
    private String moId;
    
    private String uuid;    
    
    private String mezzETHMac = "--";
    
    private String mezzIBMac = "--";
    
    private String mezzFCWWPN = "--";    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMezzHealthStatus() {
        return mezzHealthStatus;
    }

    public void setMezzHealthStatus(int mezzHealthStatus) {
        this.mezzHealthStatus = mezzHealthStatus;
    }

    public int getPresentState() {
        return presentState;
    }

    public void setPresentState(int presentState) {
        this.presentState = presentState;
    }

    public String getMezzInfo() {
        return mezzInfo;
    }

    public void setMezzInfo(String mezzInfo) {
        this.mezzInfo = mezzInfo;
    }

    public String getMezzLocation() {
        return mezzLocation;
    }

    public void setMezzLocation(String mezzLocation) {
        this.mezzLocation = mezzLocation;
    }

    public String getMezzMac() {
        return mezzMac;
    }

    public void setMezzMac(String mezzMac) {
    	
        this.mezzMac = mezzMac;
        
        if(this.mezzETHMac == null || this.mezzETHMac.length() == 0 || this.mezzMac.startsWith("0;") == false){
        	return;
        }
        
        String[] macAry = this.mezzMac.replaceAll("^0;", "").split(";");
        List<String> mezzETHMacList = new ArrayList<String>();
        List<String> mezzIBMacList = new ArrayList<String>();
        List<String> mezzFCWWPNList = new ArrayList<String>();
        for(String mac : macAry){
        	if(mac.matches("^(0|1|2)#.+")==false){
        		continue;
        	}
        	String flag = mac.substring(0, 1);
        	switch(flag){
        	case "0" : {
        		mezzETHMacList.add(mac.replaceAll("^.#", ""));
        		break;
        	}
        	case "1" : {
        		mezzFCWWPNList.add(mac.replaceAll("^.#", ""));
        		break;
        	}
        	case "2" : {
        		mezzIBMacList.add(mac.replaceAll("^.#", ""));
        		break;
        	}
        	}
        }
        
        if(mezzETHMacList.isEmpty()){
        	mezzETHMac = "--";
        } else {
        	mezzETHMac = String.join(";", mezzETHMacList).replaceAll(":", "-");
        }
        
        if(mezzFCWWPNList.isEmpty()){
        	mezzFCWWPN = "--";
        } else {
        	mezzFCWWPN = String.join(";", mezzFCWWPNList).replaceAll(":", "-");
        }
        
        if(mezzIBMacList.isEmpty()){
        	mezzIBMac = "--";
        } else {
        	mezzIBMac = String.join(";", mezzIBMacList).replaceAll(":", "-");
        }
        
    }
       
    public String getMezzETHMac() {
        return mezzETHMac;
    }



    public void setMezzETHMac(String mezzETHMac) {
        this.mezzETHMac = mezzETHMac;
    }



    public String getMezzIBMac() {
        return mezzIBMac;
    }



    public void setMezzIBMac(String mezzIBMac) {
        this.mezzIBMac = mezzIBMac;
    }



    public String getMezzFCWWPN() {
        return mezzFCWWPN;
    }



    public void setMezzFCWWPN(String mezzFCWWPN) {
        this.mezzFCWWPN = mezzFCWWPN;
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



    @Override
    public ResourceKey convert2Resource(String id, String adapterKind,
            Map<ResourceKey, List<MetricData>> metricsByResource) {
        
        ResourceKey resourceKey = new ResourceKey(this.name, Constant.KIND_MEZZ, adapterKind);
        //设定唯一的标识，保证同名的资源可以正常显示
        ResourceIdentifierConfig dnIdentifier = 
                new ResourceIdentifierConfig(Constant.ATTR_ID, id + this.uuid, true);
        resourceKey.addIdentifier(dnIdentifier);
        long timestamp = System.currentTimeMillis();
        
        List<MetricData> metricData = new ArrayList<>();
        
        //写入resource属性
        metricData.add(
                new MetricData(new MetricKey(true).add(Constant.ATTR_NAME), timestamp, this.name));
        metricData.add(
                new MetricData(new MetricKey(false).add(Constant.ATTR_MEZZ_HEALTH_STATUS), 
                        timestamp, ConvertUtils.convertHealthState(this.getMezzHealthStatus())));
        metricData.add(
                new MetricData(new MetricKey(true).add(Constant.ATTR_PRESENTSTATE), timestamp, 
                        ConvertUtils.convertPresentState(this.presentState)));
        
        metricData.add(new MetricData(new MetricKey(true).add(Constant.ATTR_MEZZ_INFO), 
                timestamp, this.mezzInfo));
        
        metricData.add(
                new MetricData(new MetricKey(true).add(Constant.ATTR_MEZZ_LOCATION), 
                        timestamp, this.mezzLocation));
        
        metricData.add(
                new MetricData(new MetricKey(true).add(Constant.ATTR_MEZZ_ETH_MAC), 
                        timestamp, this.mezzETHMac));
        
        metricData.add(
                new MetricData(new MetricKey(true).add(Constant.ATTR_MEZZ_IB_MAC), 
                        timestamp, this.mezzIBMac));
        
        metricData.add(
                new MetricData(new MetricKey(true).add(Constant.ATTR_MEZZ_FCWWPN), 
                        timestamp, this.mezzFCWWPN));
        
        metricData.add(
                new MetricData(new MetricKey(true).add(Constant.ATTR_MOID), timestamp, this.moId));
        metricData.add(
                new MetricData(new MetricKey(true).add(Constant.ATTR_UUID), timestamp, this.uuid));
        
        //关联key和属性值
        metricsByResource.put(resourceKey, metricData);
        
        return resourceKey;
    }

    

}
