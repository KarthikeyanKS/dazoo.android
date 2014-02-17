package com.mitv.test;

import org.junit.Test;

import junit.framework.Assert;

import com.google.gson.Gson;
import com.millicom.mitv.models.gson.AppConfigurationData;
import com.mitv.Consts;

public class AppConfigGSONTest extends Tests {
	
	private AppConfigurationData appConfigData;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		String jsonString = super.deserializeJson(Consts.URL_CONFIGURATION);
		appConfigData = new Gson().fromJson(jsonString, AppConfigurationData.class);
	}

	@Test
	public void testNotNull() {
		Assert.assertNotNull(appConfigData);
	}
	
	@Test
	public void testAllVariablesNotNull() {
		Assert.assertNotNull(appConfigData);
		
		Assert.assertNotNull(appConfigData.getAdzerkNetworkId());
		//Assert.assertFalse(TextUtils.isEmpty(appConfigDatas.getAdzerkNetworkId()));
		
		Assert.assertNotNull(appConfigData.getAdzerkSiteId());
		//Assert.assertFalse(TextUtils.isEmpty(appConfigDatas.getAdzerkSiteId()));
		
		/* TODO more tests for each ... */
		
	}
}
