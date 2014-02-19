
package com.mitv.test;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;
import com.google.gson.Gson;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.models.gson.TVChannelId;
import com.millicom.mitv.models.gson.UserLoginData;
import com.millicom.mitv.models.gson.serialization.UserTVChannelIdsData;
import com.mitv.Consts;



public class SetUserTVChannelsIdsTest 
	extends TestCore
{
	@SuppressWarnings("unused")
	private static final String	TAG	= "SetUserTVChannelsIdsTest";
	
	private HTTPCoreResponse httpCoreResponse;
	
	
	
	@Override
	protected void setUp()
			throws Exception 
	{
		super.setUp();
		
		TVChannelId tvChannelId = new TVChannelId("5393752d-6df3-4673-989b-85b78d1bf7bc");
		
		List<TVChannelId> channelIds = new ArrayList<TVChannelId>();
		channelIds.add(tvChannelId);
		
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
		
		httpCoreResponse = setUserTVChannelIds(token, channelIds);
	}
	
	
	
	private HTTPCoreResponse setUserTVChannelIds(
			String token,
			List<TVChannelId> channelIds)
	{
		SetUserTVChannelsIdsTest instance = new SetUserTVChannelsIdsTest();
				
		String url = Consts.URL_MY_CHANNEL_IDS;
		
		URLParameters urlParameters = new URLParameters();
		
		Map<String, String> headerParameters = getHeaderParametersWithUserToken(token);
		
		UserTVChannelIdsData postData = new UserTVChannelIdsData();
		postData.setChannelIds(channelIds);
		
		String bodyContentData = new Gson().toJson(postData);
		
		HTTPCoreResponse httpCoreResponse = instance.executeRequest(HTTPRequestTypeEnum.HTTP_POST, url, urlParameters, headerParameters, bodyContentData);
				
		return httpCoreResponse;
	}
	
	

	@Test
	public void testReponseIsOK()
	{
		Assert.assertFalse(httpCoreResponse.getStatusCode() != FetchRequestResultEnum.SUCCESS_WITH_NO_CONTENT.getStatusCode());
	}
}