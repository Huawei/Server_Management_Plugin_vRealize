package com.huawei.esight.service.bean;

import org.junit.Assert;
import org.junit.Test;


public class MezzBeanTest {
	
	@Test
	public void testMacConvertion(){
		MezzBean bean = new MezzBean();
		bean.setMezzMac("0;1#666;2#test1;0#18:DE:D7:88:C3:3F;");
		Assert.assertArrayEquals("", "test1".getBytes(), bean.getMezzIBMac().getBytes());
		Assert.assertArrayEquals("", "18-DE-D7-88-C3-3F".getBytes(), bean.getMezzETHMac().getBytes());
		Assert.assertArrayEquals("", "666".getBytes(), bean.getMezzFCWWPN().getBytes());
		
		bean = new MezzBean();
		bean.setMezzMac("dummy-mac");
		Assert.assertArrayEquals("", "--".getBytes(), bean.getMezzIBMac().getBytes());
		Assert.assertArrayEquals("", "--".getBytes(), bean.getMezzETHMac().getBytes());
		Assert.assertArrayEquals("", "--".getBytes(), bean.getMezzFCWWPN().getBytes());
	}

}
