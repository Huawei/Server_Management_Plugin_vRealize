package com.huawei.esight.service.bean;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class CalculateHealthTest {
	
	@Test
	public void testCalculateHealth(){
		
		List<String> childStatusList = new ArrayList<>();
		Assert.assertTrue(calculate(childStatusList).equals("OK"));
    	
		childStatusList.add("Unknown");
		Assert.assertTrue(calculate(childStatusList).equals("Unknown"));
    	
		childStatusList.add("Warning");
		Assert.assertTrue(calculate(childStatusList).equals("Warning"));
		
		childStatusList.add("Immediate");
		Assert.assertTrue(calculate(childStatusList).equals("Immediate"));
		
		childStatusList.add("Critical");
		Assert.assertTrue(calculate(childStatusList).equals("Critical"));
	}
	
	String calculate(List<String> childStatusList){
		
		String defaultStatus = "OK";
    	for(String status: childStatusList){
    		switch (defaultStatus){
    		case "OK":{
    			defaultStatus = status;
    		}
    		break;
    		case "Unknown":{
    			if(status.equals("Warning")||status.equals("Immediate")||status.equals("Critical")){
    				defaultStatus = status;
    			}
    		}
    		break;
    		case "Warning":{
    			if(status.equals("Immediate")||status.equals("Critical")){
    				defaultStatus = status;
    			}
    		}
    		break;
    		case "Immediate":{
    			if(status.equals("Critical")){
    				defaultStatus = status;
    			}
    		}
    		break;
    		case "Critical":{
    			break;
    		}
    		}
    	}
		
		return defaultStatus;
		
	}

}
