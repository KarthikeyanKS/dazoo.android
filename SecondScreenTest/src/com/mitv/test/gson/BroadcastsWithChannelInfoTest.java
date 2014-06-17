

package com.mitv.test.gson;



import junit.framework.Assert;

import org.junit.Test;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mitv.Constants;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.models.objects.mitvapi.TVBroadcast;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVChannelGuide;



/**
 * This class tests the fetched broadcast detailed information with channel info. Returns a list of fields from broadcast and channel object.
 * 
 * GET /epg/channels/{channelId}/broadcasts/{beginTimeMillis}
 * 
 * @author atsampikakis
 *
 */
public class BroadcastsWithChannelInfoTest 
	extends TestBaseWithGuide 
{
	private static final String	TAG	= "TVBroadcastDetailsTest";
	
	private TVBroadcastWithChannelInfo tvBroadcastWithChannelInfo;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
				
		TVChannelGuide someGuide = tvChannelGuides.get(0);
		TVBroadcast broadcast = someGuide.getBroadcasts().get(0);
		String channelId = someGuide.getChannelId().getChannelId();
		Long beginTimeMillis = broadcast.getBeginTimeMillis();
		
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.URL_CHANNELS_ALL);
		sb.append(Constants.FORWARD_SLASH);
		sb.append(channelId);
		sb.append(Constants.API_BROADCASTS);
		sb.append(Constants.FORWARD_SLASH);
		sb.append(beginTimeMillis);
		String url = sb.toString();
		
		HTTPCoreResponse httpCoreResponse = executeRequestGet(url);
		
		String jsonString = httpCoreResponse.getResponseString();
				
		try
		{
			tvBroadcastWithChannelInfo = new Gson().fromJson(jsonString, TVBroadcastWithChannelInfo.class);
		}
		catch(JsonSyntaxException jsex)
		{
			Log.e(TAG, jsex.getMessage(), jsex);
		}
	}

	
	
	@Test
	public void testNotNull() 
	{
		Assert.assertNotNull(tvBroadcastWithChannelInfo);
	}
	
	
	
	@Test
	public void testAllVariablesNotNull()
	{
		assertTrue(tvBroadcastWithChannelInfo.areDataFieldsValid());
	}			
}
