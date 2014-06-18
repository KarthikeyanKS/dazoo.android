package com.mitv.test.gson;



import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.models.gson.mitvapi.AppVersionJSON;



public class AppVersionTest 
	extends TestCore 
{
	private List<AppVersionJSON> receivedData;
	

	
	@Override
	protected void setUp() 
			throws Exception
	{
		super.setUp();
		
		String url = Constants.URL_API_VERSION;
		
		HTTPCoreResponse httpCoreResponse = executeRequest(HTTPRequestTypeEnum.HTTP_GET, url);
		
		String responseString = httpCoreResponse.getResponseString();
		
		receivedData = Arrays.asList(new Gson().fromJson(responseString, AppVersionJSON[].class));
	}

	
	
	@Test
	public void testNotNull()
	{
		Assert.assertNotNull(receivedData);
		Assert.assertFalse(receivedData.isEmpty());
	}
	
	
	
	@Test
	public void testAllVariablesNotNull() 
	{
		for(AppVersionJSON appVersionDataParts : receivedData) 
		{
			Assert.assertNotNull(receivedData);
			
			Assert.assertNotNull(appVersionDataParts.getName());
			Assert.assertFalse(TextUtils.isEmpty(appVersionDataParts.getName()));
			
			Assert.assertNotNull(appVersionDataParts.getValue());
			Assert.assertFalse(TextUtils.isEmpty(appVersionDataParts.getValue()));
		}
	}
}