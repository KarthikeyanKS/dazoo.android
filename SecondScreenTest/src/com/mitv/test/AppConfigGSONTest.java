
package com.mitv.test;



import org.junit.Test;
import junit.framework.Assert;
import com.google.gson.Gson;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.models.gson.AppConfigurationData;
import com.mitv.Consts;



public class AppConfigGSONTest 
	extends Tests
{	
	private AppConfigurationData appConfigData;

	
	
	@Override
	protected void setUp() 
		throws Exception 
	{
		super.setUp();
		
		String url = Consts.URL_CONFIGURATION;
		
		HTTPCoreResponse httpCoreResponse = executeRequest(HTTPRequestTypeEnum.HTTP_GET, url);
		
		String responseString = httpCoreResponse.getResponseString();
		
		appConfigData = new Gson().fromJson(responseString, AppConfigurationData.class);
	}

	
	
	@Test
	public void testNotNull() 
	{
		Assert.assertNotNull(appConfigData);
	}
	
	
	
	@Test
	public void testAllVariablesNotNull()
	{
		Assert.assertNotNull(appConfigData);
		
		Assert.assertNotNull(appConfigData.getAdzerkNetworkId());
		//Assert.assertFalse(TextUtils.isEmpty(appConfigDatas.getAdzerkNetworkId()));
		
		Assert.assertNotNull(appConfigData.getAdzerkSiteId());
		//Assert.assertFalse(TextUtils.isEmpty(appConfigDatas.getAdzerkSiteId()));
		
		/* TODO more tests for each ... */	
	}
}