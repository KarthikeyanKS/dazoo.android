
package com.mitv.test;



import junit.framework.Assert;

import org.junit.Test;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.models.AppConfiguration;
import com.mitv.Consts;



public class AppConfigurationTest 
	extends TestCore
{
	private static final String	TAG	= "AppConfigurationTest";
	
	private AppConfiguration receivedData;

	
	
	@Override
	protected void setUp() 
		throws Exception 
	{
		super.setUp();
		
		String url = Consts.URL_CONFIGURATION;
		
		HTTPCoreResponse httpCoreResponse = executeRequest(HTTPRequestTypeEnum.HTTP_GET, url);
		
		String responseString = httpCoreResponse.getResponseString();
		
		try
		{
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(AppConfiguration.class, new AppConfiguration());
			gsonBuilder.excludeFieldsWithoutExposeAnnotation();
			
			Gson gson = gsonBuilder.create();
			
			receivedData = gson.fromJson(responseString, AppConfiguration.class);
		}
		catch(JsonSyntaxException jsex)
		{
			Log.e(TAG, jsex.getMessage(), jsex);
			
			receivedData = null;
		}
	}

	
	
	@Test
	public void testNotNull() 
	{
		Assert.assertNotNull(receivedData);
	}
	
	
	
	@Test
	public void testAllVariablesNotNull()
	{
		Assert.assertNotNull(receivedData);
		
		Assert.assertNotNull(receivedData.getFirstHourOfDay());
		
		Assert.assertNotNull(receivedData.getWelcomeToast());
		
		Assert.assertNotNull(receivedData.isAdsEnabled());
		
		Assert.assertNotNull(receivedData.getAdzerkNetworkId());
		
		Assert.assertNotNull(receivedData.getAdzerkSiteId());
		
		Assert.assertNotNull(receivedData.getAdzerkLevel());
		
		Assert.assertNotNull(receivedData.getAdzerkFormats());
		Assert.assertFalse(receivedData.getAdzerkFormats().isEmpty());

		Assert.assertNotNull(receivedData.getAdzerkFormatsForActivity());
		Assert.assertFalse(receivedData.getAdzerkFormatsForActivity().isEmpty());
		
		Assert.assertNotNull(receivedData.getAdzerkFormatsForAndroidGuide());
		Assert.assertFalse(receivedData.getAdzerkFormatsForAndroidGuide().isEmpty());		
		
		Assert.assertNotNull(receivedData.getGoogleAnalyticsSampleRate());
		
		Assert.assertNotNull(receivedData.isGoogleAnalyticsEnabled());
		
		Assert.assertNotNull(receivedData.getGoogleAnalyticsTrackingId());
		Assert.assertFalse(TextUtils.isEmpty(receivedData.getGoogleAnalyticsTrackingId()));

		Assert.assertNotNull(receivedData.getActivityCellCountBetweenAdCells());
		
		Assert.assertNotNull(receivedData.getCellCountBetweenAdCells());

		Assert.assertNotNull(receivedData.getFacebookAppID());
		Assert.assertFalse(TextUtils.isEmpty(receivedData.getFacebookAppID()));
		
		Assert.assertNotNull(receivedData.getTrackingDomain());
		Assert.assertFalse(TextUtils.isEmpty(receivedData.getTrackingDomain()));
	}
}