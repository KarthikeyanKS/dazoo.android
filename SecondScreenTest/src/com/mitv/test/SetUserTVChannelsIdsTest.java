
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
import com.millicom.mitv.models.TVChannelId;
import com.mitv.Consts;



public class SetUserTVChannelsIdsTest 
	extends TestBaseWithGuide
{
	@SuppressWarnings("unused")
	private static final String	TAG	= "SetUserTVChannelsIdsTest";
	
	/* CHILDREN */
	private static final String TV_CHANNEL_ID_NICKELODEON = "42994506-3e64-4d31-b268-c5c4e7e7c00c";
	
	/* SERIES */
	private static final String TV_CHANNEL_ID_HBO = "708d90ab-8e3a-422e-90df-85f51852e114";
	private static final String TV_CHANNEL_ID_AXN = "6eb7eacd-7956-4ff8-a326-7d4b201bbfc6";
	
	/* SPORTS */
	private static final String TV_CHANNEL_ID_ESPN = "ce96a0cf-ce34-41ef-85b5-e8d0446ecd6b";
	
	/* MOVIES */
	private static final String TV_CHANNEL_ID_CINEMA_PLUS = "eebe4ecb-412d-4c16-b11a-56709b8df4fe";
	private static final String TV_CHANNEL_ID_FILM_ZONE = "dd6d36f9-a2d3-416d-b72d-194624d322e0";
	
	
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
		SetUserTVChannelsIdsTest instance = new SetUserTVChannelsIdsTest();
				
		String url = Consts.URL_MY_CHANNEL_IDS;
		
		URLParameters urlParameters = new URLParameters();
		
		Map<String, String> headerParameters = getHeaderParametersWithUserToken(token);
		
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