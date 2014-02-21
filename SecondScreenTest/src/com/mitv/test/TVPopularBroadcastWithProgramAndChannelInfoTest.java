package com.mitv.test;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.models.gson.TVBroadcastWithProgramAndChannelInfo;
import com.millicom.mitv.models.gson.TVChannel;
import com.millicom.mitv.models.gson.TVProgram;
import com.mitv.Consts;


/**
 * This class tests the broadcast popular. Returns a list of objects containing channel object, program object and fields from broadcast.
 * 
 * GET /epg/broadcasts/popular
 * 
 * @author atsampikakis
 *
 */
public class TVPopularBroadcastWithProgramAndChannelInfoTest 
	extends TestCore 
{
	private static final String	TAG	= "TVBroadcastDetailsTest";
	
	private List<TVBroadcastWithProgramAndChannelInfo> tvPopularBroadcastsWithChannelAndProgramInfo;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		String url = Consts.URL_POPULAR;
		
		HTTPCoreResponse httpCoreResponse = executeRequestGet(url);
		
		String jsonString = httpCoreResponse.getResponseString();
				
		try
		{
			tvPopularBroadcastsWithChannelAndProgramInfo = Arrays.asList(new Gson().fromJson(jsonString, TVBroadcastWithProgramAndChannelInfo[].class));
		}
		catch(JsonSyntaxException jsex)
		{
			Log.e(TAG, jsex.getMessage(), jsex);
		}
	}

	@Test
	public void testNotNull() {
		Assert.assertNotNull(tvPopularBroadcastsWithChannelAndProgramInfo);
		Assert.assertFalse(tvPopularBroadcastsWithChannelAndProgramInfo.isEmpty());
	}
	
	@Test
	public void testAllVariablesNotNull() {
		for(TVBroadcastWithProgramAndChannelInfo popular : tvPopularBroadcastsWithChannelAndProgramInfo) {
			TVBroadcastWithChannelInfoTest.testBroadcast(popular);
			
			TVChannel tvChannel = popular.getChannel();
			TVChannelGSONTest.testTVChannelObject(tvChannel);
			
			TVProgram tvProgram = popular.getProgram();
			TVBroadcastWithProgramAndChannelInfoTest.testTVProgram(tvProgram);
		}
	}
}
