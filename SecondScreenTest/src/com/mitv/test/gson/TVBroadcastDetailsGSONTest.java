package com.mitv.test.gson;

import junit.framework.Assert;

import org.junit.Test;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mitv.Constants;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.models.TVBroadcast;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.models.TVChannelGuide;


/**
 * This class tests the fetched broadcast detailed information. Returns objects with field from broadcast, channel object and program object.
 * 
 * GET /epg/channels/{channelId}/broadcasts/{beginTimeMillis}
 * 
 * @author atsampikakis
 *
 */
public class TVBroadcastDetailsGSONTest 
	extends TestBaseWithGuide 
{
	private static final String	TAG	= "TVBroadcastDetailsTest";
	
	private TVBroadcastWithChannelInfo tvBroadcastDetails;
	
	
	
	@Override
	protected void setUp() 
			throws Exception 
	{
		super.setUp();

		TVChannelGuide someGuide = tvChannelGuides.get(0);
		TVBroadcast broadcast = someGuide.getBroadcasts().get(0);
		String channelId = someGuide.getChannelId().getChannelId();
		Long beginTimeMillis = broadcast.getBeginTimeMillis();
		
		StringBuilder url = new StringBuilder();
		url.append(Constants.URL_CHANNELS_ALL);
		url.append(Constants.REQUEST_QUERY_SEPARATOR);
		url.append(channelId);
		url.append(Constants.API_BROADCASTS);
		url.append(Constants.REQUEST_QUERY_SEPARATOR);
		url.append(beginTimeMillis);
		
		HTTPCoreResponse httpCoreResponse = executeRequest(HTTPRequestTypeEnum.HTTP_GET, url.toString());
		
		String responseString = httpCoreResponse.getResponseString();
		
		try
		{
			tvBroadcastDetails = new Gson().fromJson(responseString, TVBroadcastWithChannelInfo.class);
		}
		catch(JsonSyntaxException jsex)
		{
			Log.e(TAG, jsex.getMessage(), jsex);
		}
	}

	
	
	@Test
	public void testNotNull()
	{
		Assert.assertNotNull(tvBroadcastDetails);
	}
	
	
	
	@Test
	public void testAllVariablesNotNull()
	{	
		Assert.assertNotNull(tvBroadcastDetails.getChannel());
		
		//Assert.assertNotNull(tvBroadcastDetails.getTvProgram());
	}
}