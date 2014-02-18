
package com.mitv.test;



import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.google.gson.Gson;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.models.gson.Broadcast;
import com.millicom.mitv.models.gson.TVChannelGuide;
import com.millicom.mitv.models.gson.TVChannelId;
import com.millicom.mitv.models.gson.TVDate;
import com.mitv.Consts;

public class TVChannelGuidesGSONTest 
	extends TestCore
{	
	private List<TVChannelGuide> tvChannelGuides;

	
	@Override
	protected void setUp() 
			throws Exception 
	{
		super.setUp();
		tvChannelGuides = testFetchTVChannelGuides();
	}

	public static List<TVChannelGuide> testFetchTVChannelGuides() {
		TVChannelGuidesGSONTest instance = new TVChannelGuidesGSONTest();

		List<TVDate> tvDates = TVDatesGSONTest.testFetchTVDates();
		TVDate tvDateToday = tvDates.get(0);
		
		StringBuilder sb = new StringBuilder(Consts.URL_GUIDE);
		sb.append(tvDateToday.getId());
		String url = sb.toString();
		
		List<TVChannelId> tvChannelIdsUser = GetUserTVChannelIdsTest.getUserTVChannelIds();
		
		URLParameters urlParameters = new URLParameters();
				
		for(TVChannelId tvChannelId : tvChannelIdsUser) 
		{
			String tvChannelIdAsString = tvChannelId.getChannelId();
			urlParameters.add(Consts.API_CHANNEL_ID, tvChannelIdAsString);
		}
		
		HTTPCoreResponse httpCoreResponse = instance.executeRequestNoBody(HTTPRequestTypeEnum.HTTP_GET, url, urlParameters);

		String jsonString = httpCoreResponse.getResponseString();

		List<TVChannelGuide> tvChannelGuides = Arrays.asList(new Gson().fromJson(jsonString,
				TVChannelGuide[].class));

		return tvChannelGuides;
	}

	
	
	@Test
	public void testNotNull() 
	{
		Assert.assertNotNull(tvChannelGuides);
		Assert.assertFalse(tvChannelGuides.isEmpty());
	}

	@Test
	public void testAllVariablesNotNull() {
		Assert.assertNotNull(tvChannelGuides);
		for (TVChannelGuide tvChannelGuide : tvChannelGuides) {
			List<Broadcast> broadcastsForTVChannel = tvChannelGuide.getBroadcasts();
			for(Broadcast broadcast : broadcastsForTVChannel) {
				TVBroadcastWithChannelInfoTest.testBroadcast(broadcast);
			}
		}
	}

}
