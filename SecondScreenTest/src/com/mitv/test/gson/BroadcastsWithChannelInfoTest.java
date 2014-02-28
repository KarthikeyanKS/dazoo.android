package com.mitv.test.gson;

import junit.framework.Assert;

import org.junit.Test;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.models.ImageSetOrientation;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVChannel;
import com.millicom.mitv.models.TVChannelGuide;
import com.millicom.mitv.models.TVCredit;
import com.millicom.mitv.models.TVProgram;
import com.millicom.mitv.models.TVSeries;
import com.millicom.mitv.models.TVSeriesSeason;
import com.mitv.Consts;


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
		sb.append(Consts.URL_CHANNELS_ALL);
		sb.append(Consts.REQUEST_QUERY_SEPARATOR);
		sb.append(channelId);
		sb.append(Consts.API_BROADCASTS);
		sb.append(Consts.REQUEST_QUERY_SEPARATOR);
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
	public void testNotNull() {
		Assert.assertNotNull(tvBroadcastWithChannelInfo);
	}
	
	@Test
	public void testAllVariablesNotNull() {
		assertTrue(tvBroadcastWithChannelInfo.areDataFieldsValid());
	}
					
}
