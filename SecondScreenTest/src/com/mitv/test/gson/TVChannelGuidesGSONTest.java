
package com.mitv.test.gson;



import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.google.gson.Gson;
import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.http.URLParameters;
import com.mitv.models.objects.mitvapi.TVBroadcast;
import com.mitv.models.objects.mitvapi.TVChannelGuide;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.TVDate;

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
		
		StringBuilder sb = new StringBuilder(Constants.URL_GUIDE);
		sb.append(Constants.REQUEST_QUERY_SEPARATOR);
		sb.append(tvDateToday.getId());
		String url = sb.toString();
		
		
		URLParameters urlParameters = new URLParameters();
				
		for(TVChannelId tvChannelId : tvChannelIdsUser) 
		{
			String tvChannelIdAsString = tvChannelId.getChannelId();
			urlParameters.add(Constants.API_CHANNEL_ID, tvChannelIdAsString);
		}
		
		HTTPCoreResponse httpCoreResponse = instance.executeRequestNoBody(HTTPRequestTypeEnum.HTTP_GET, url, urlParameters);

		String jsonString = httpCoreResponse.getResponseString();

		List<TVChannelGuide> tvChannelGuides = Arrays.asList(new Gson().fromJson(jsonString,
				TVChannelGuide[].class));

		return tvChannelGuides;
	}


	@Test
	public void testAllVariablesNotNull() {
		Assert.assertNotNull(tvChannelGuides);
		Assert.assertFalse(tvChannelGuides.isEmpty());
		for (TVChannelGuide tvChannelGuide : tvChannelGuides) {
			List<TVBroadcast> broadcastsForTVChannel = tvChannelGuide.getBroadcasts();
			for(TVBroadcast broadcast : broadcastsForTVChannel) {
				assertTrue(broadcast.areDataFieldsValid());
			}
		}
	}

}
