
package com.mitv.test.gson;



import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.http.HeaderParameters;
import com.mitv.http.URLParameters;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.UserLoginData;



public class GetUserTVChannelIdsTest 
	extends TestCore
{
	private static final String	TAG	= "PerformUserLoginTest";
	
	private List<TVChannelId> receivedData;
	
	
	
	@Override
	protected void setUp()
			throws Exception 
	{
		super.setUp();
		
		receivedData = getUserTVChannelIds();
	}
	
	public static List<TVChannelId> getUserTVChannelIds() {
		String token = getUserToken();
		return getUserTVChannelIds(token);
	}
	
	public static String getUserToken() {
		String token = "";
		
		if(token.isEmpty())
		{
			UserLoginData data = PerformUserLoginTest.login();
			
			if(data != null)
			{
				token = data.getToken();
			}
			// No need for else
		}
		
		return token;
	}
	
	private static List<TVChannelId> getUserTVChannelIds(String token)
	{
		GetUserTVChannelIdsTest instance = new GetUserTVChannelIdsTest();
		
		List<TVChannelId> receivedData;
		
		String url = Constants.URL_MY_CHANNEL_IDS;
		
		URLParameters urlParameters = new URLParameters();
		
		HeaderParameters headerParameters = getHeaderParametersWithUserToken(token);
		
		HTTPCoreResponse httpCoreResponse = instance.executeRequest(HTTPRequestTypeEnum.HTTP_GET, url, urlParameters, headerParameters);
		
		String responseString = httpCoreResponse.getResponseString();
				
		try
		{
			receivedData = Arrays.asList(new Gson().fromJson(responseString, TVChannelId[].class));
		}
		catch(JsonSyntaxException jsex)
		{
			Log.e(TAG, jsex.getMessage(), jsex);
			
			receivedData = null;
		}
		
		return receivedData;
	}
	
	

	@Test
	public void testNotNullOrEmpty()
	{	
		Assert.assertNotNull(receivedData);
		Assert.assertFalse(receivedData.isEmpty());
	}
	
	
	
	@Test
	public void testAllVariablesNotNullOrEmpty() 
	{
		for(TVChannelId tvChannelId : receivedData) 
		{
			Assert.assertNotNull(tvChannelId.getChannelId());
			Assert.assertFalse(TextUtils.isEmpty(tvChannelId.getChannelId()));
		}
	}
}