
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
import com.mitv.models.objects.mitvapi.TVFeedItem;
import com.mitv.models.objects.mitvapi.UserLoginData;



public class GetUserTVFeedItemsTest
	extends TestCore
{
	private static final String	TAG	= "GetTVFeedItemsTest";
	
	private List<TVFeedItem> receivedData;
	
	
	
	@Override
	protected void setUp()
			throws Exception 
	{
		super.setUp();
		
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
		// No need for else
		
		receivedData = getUserTVFeedItems(token);
	}
	
	
	
	private static List<TVFeedItem> getUserTVFeedItems(String token)
	{
		GetUserTVChannelIdsTest instance = new GetUserTVChannelIdsTest();
		
		List<TVFeedItem> receivedData;
		
		String url = Constants.URL_ACTIVITY_FEED;
		
		URLParameters urlParameters = new URLParameters();
		
		HeaderParameters headerParameters = getHeaderParametersWithUserToken(token);
		
		HTTPCoreResponse httpCoreResponse = instance.executeRequest(HTTPRequestTypeEnum.HTTP_GET, url, urlParameters, headerParameters);
		
		String responseString = httpCoreResponse.getResponseString();
				
		try
		{
			receivedData = Arrays.asList(new Gson().fromJson(responseString, TVFeedItem[].class));
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
		for(TVFeedItem tvFeedItem : receivedData) 
		{
			Assert.assertNotNull(tvFeedItem.getTitle());
			Assert.assertFalse(TextUtils.isEmpty(tvFeedItem.getTitle()));
			
			Assert.assertNotNull(tvFeedItem.getItemType());
			
			switch (tvFeedItem.getItemType())
			{
				case BROADCAST:
				case POPULAR_BROADCAST:
				case RECOMMENDED_BROADCAST:
				case POPULAR_TWITTER:
				{
					Assert.assertNotNull(tvFeedItem.getBroadcast());
				}
				break;
				
				case POPULAR_BROADCASTS:
				{
					Assert.assertNotNull(tvFeedItem.getBroadcasts());
					Assert.assertFalse(tvFeedItem.getBroadcasts().isEmpty());
				}
				break;
				
				case UNKNOWN:
				default:
				{
					Assert.assertFalse(true);
				}
				break;
			}
		}
	}
}