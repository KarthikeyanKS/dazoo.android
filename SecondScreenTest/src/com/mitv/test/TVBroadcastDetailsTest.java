package com.mitv.test;

import junit.framework.Assert;

import org.junit.Test;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.gson.TVChannelGuide;
import com.mitv.Consts;


/**
 * This class tests the fetched broadcast detailed information. Returns objects with field from broadcast, channel object and program object.
 * 
 * GET /epg/channels/{channelId}/broadcasts/{beginTimeMillis}
 * 
 * @author atsampikakis
 *
 */
public class TVBroadcastDetailsTest 
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
		url.append(Consts.URL_CHANNELS_ALL);
		url.append(Consts.REQUEST_QUERY_SEPARATOR);
		url.append(channelId);
		url.append(Consts.API_BROADCASTS);
		url.append(Consts.REQUEST_QUERY_SEPARATOR);
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