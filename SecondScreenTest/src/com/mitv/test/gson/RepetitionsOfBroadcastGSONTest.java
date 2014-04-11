package com.mitv.test.gson;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mitv.Constants;
import com.mitv.http.HTTPCoreResponse;
import com.mitv.models.TVBroadcast;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.models.TVChannelGuide;
import com.mitv.models.TVProgram;

/**
 * This class tests the fetch broadcasts from program with channel info. Returns a list of fields from broadcast and channel object.
 * 
 * GET /epg/programs/{programid}/broadcasts
 * 
 * @author atsampikakis
 *
 */
public class RepetitionsOfBroadcastGSONTest extends TestBaseWithGuide {
	
	private List<TVBroadcastWithChannelInfo> broadcastsWithChannelInfo;
	private static final String	TAG	= "TVBroadcastProgramTest";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		TVChannelGuide someGuide = tvChannelGuides.get(0);
		TVBroadcast broadcast = someGuide.getBroadcasts().get(0);
		TVProgram tvProgram = broadcast.getProgram();
		String programId = tvProgram.getProgramId();
		
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.URL_PROGRAMS);
		sb.append(programId);
		sb.append(Constants.API_BROADCASTS);
		String url = sb.toString();
				
		HTTPCoreResponse httpCoreResponse = executeRequestGet(url);
		
		String jsonString = httpCoreResponse.getResponseString();
				
		try
		{
			broadcastsWithChannelInfo = Arrays.asList(new Gson().fromJson(jsonString, TVBroadcastWithChannelInfo[].class));
		}
		catch(JsonSyntaxException jsex)
		{
			Log.e(TAG, jsex.getMessage(), jsex);
		}
	}

	@Test
	public void testNotNull() {
		Assert.assertNotNull(broadcastsWithChannelInfo);
	}
	
	@Test
	public void testAllVariablesNotNull() {
		for(TVBroadcastWithChannelInfo broadcastWithChannelInfo : broadcastsWithChannelInfo) {
			assertTrue(broadcastWithChannelInfo.areDataFieldsValid());
		}
	}

}
