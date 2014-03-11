
package com.mitv.test.gson;



import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.google.gson.Gson;
import com.mitv.Constants;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.http.HeaderParameters;
import com.mitv.http.URLParameters;
import com.mitv.models.TVChannelId;



public class SetUserTVChannelsIdsGSONTest 
	extends TestBaseWithGuide
{
	@SuppressWarnings("unused")
	private static final String	TAG	= "SetUserTVChannelsIdsTest";
	
	/* CHILDREN */
	private static final String TV_CHANNEL_ID_NICKELODEON = "co_0f903a27-84ea-4117-a6aa-7777d7fd7b1d";
	
	/* SERIES */
	private static final String TV_CHANNEL_ID_HBO = "co_6b1df59d-bb2d-455e-a007-8565788bc048";
	private static final String TV_CHANNEL_ID_AXN = "co_57df7605-098f-47a0-a36d-8148f7f36ca8";
	
	/* SPORTS */
	private static final String TV_CHANNEL_ID_ESPN = "co_8a59310b-f558-48e3-8dcf-d59afc9e5ac3";
	
	/* MOVIES */
	private static final String TV_CHANNEL_ID_CINEMA_PLUS = "co_1745af76-5a69-42b0-94bf-2021d548e108";
	private static final String TV_CHANNEL_ID_FILM_ZONE = "co_a36f4478-eddf-406f-96b5-76eb88b358fd";
	
	
	private HTTPCoreResponse httpCoreResponse;
	
	@Override
	protected void setUp()
			throws Exception 
	{
		super.setUp();

		List<TVChannelId> channelIds = new ArrayList<TVChannelId>();
		TVChannelId tvChannelId = new TVChannelId(TV_CHANNEL_ID_NICKELODEON);
		channelIds.add(tvChannelId);
		tvChannelId = new TVChannelId(TV_CHANNEL_ID_HBO);
		channelIds.add(tvChannelId);
		tvChannelId = new TVChannelId(TV_CHANNEL_ID_AXN);
		channelIds.add(tvChannelId);
		tvChannelId = new TVChannelId(TV_CHANNEL_ID_ESPN);
		channelIds.add(tvChannelId);
		tvChannelId = new TVChannelId(TV_CHANNEL_ID_CINEMA_PLUS);
		channelIds.add(tvChannelId);
		tvChannelId = new TVChannelId(TV_CHANNEL_ID_FILM_ZONE);
		channelIds.add(tvChannelId);
		
		httpCoreResponse = setUserTVChannelIds(userToken, channelIds);
	}
	
	
	
	private HTTPCoreResponse setUserTVChannelIds(
			String token,
			List<TVChannelId> channelIds)
	{
		SetUserTVChannelsIdsGSONTest instance = new SetUserTVChannelsIdsGSONTest();
				
		String url = Constants.URL_MY_CHANNEL_IDS;
		
		URLParameters urlParameters = new URLParameters();
		
		HeaderParameters headerParameters = getHeaderParametersWithUserToken(token);
		
		String bodyContentData = new Gson().toJson(channelIds);
		
		HTTPCoreResponse httpCoreResponse = instance.executeRequest(HTTPRequestTypeEnum.HTTP_POST, url, urlParameters, headerParameters, bodyContentData);
				
		return httpCoreResponse;
	}
	
	

	@Test
	public void testReponseIsOK()
	{
		Assert.assertFalse(httpCoreResponse.getStatusCode() != FetchRequestResultEnum.SUCCESS_WITH_NO_CONTENT.getStatusCode());
	}
}