
package com.mitv.test;



import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.google.gson.Gson;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.http.URLParameters;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.gson.TVChannelGuide;
import com.millicom.mitv.models.gson.TVChannelId;
import com.millicom.mitv.models.gson.TVDate;
import com.mitv.Consts;

/**
 * This class tests the fetching Guide data. Returns a list of objects containing fields from channel, list of broadcasts (with fields from broadcast and program object).
 * 
 * GET /epg/guide/{date}?timeZoneOffset=&channelId=&channelId=
 * 
 * @author atsampikakis
 *
 */
public class TVChannelGuidesGSONTest 
	extends TestBaseWithGuide
{	
		
	@Override
	protected void setUp() 
			throws Exception 
	{
		super.setUp();
	}

	public static List<TVChannelGuide> testFetchTVChannelGuides() {
		TVChannelGuidesGSONTest instance = new TVChannelGuidesGSONTest();

		getTVDates();
		getTVChannelIdsUser();
		
		TVDate tvDateToday = tvDates.get(0);
		
		StringBuilder sb = new StringBuilder(Consts.URL_GUIDE);
		sb.append(Consts.REQUEST_QUERY_SEPARATOR);
		sb.append(tvDateToday.getId());
		String url = sb.toString();
		
		
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
			List<TVBroadcast> broadcastsForTVChannel = tvChannelGuide.getBroadcasts();
			for(TVBroadcast broadcast : broadcastsForTVChannel) {
				BroadcastsForSpecificProgramTest.testBroadcast(broadcast);
			}
		}
	}

}
