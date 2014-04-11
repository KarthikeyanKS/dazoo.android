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
import com.mitv.models.TVBroadcastWithChannelInfo;


/**
 * This class tests the broadcast popular. Returns a list of objects containing channel object, program object and fields from broadcast.
 * 
 * GET /epg/broadcasts/popular
 * 
 * @author atsampikakis
 *
 */
public class PopularBroadcastGSONTest 
	extends TestCore 
{
	private static final String	TAG	= "TVBroadcastDetailsTest";
	
	private List<TVBroadcastWithChannelInfo> tvPopularBroadcastsWithChannelInfo;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		String url = Constants.URL_POPULAR;
		
		HTTPCoreResponse httpCoreResponse = executeRequestGet(url);
		
		String jsonString = httpCoreResponse.getResponseString();
				
		try
		{
			tvPopularBroadcastsWithChannelInfo = Arrays.asList(new Gson().fromJson(jsonString, TVBroadcastWithChannelInfo[].class));
		}
		catch(JsonSyntaxException jsex)
		{
			Log.e(TAG, jsex.getMessage(), jsex);
		}
	}

	@Test
	public void testNotNull() {
		Assert.assertNotNull(tvPopularBroadcastsWithChannelInfo);
		Assert.assertFalse(tvPopularBroadcastsWithChannelInfo.isEmpty());
	}
	
	@Test
	public void testAllVariablesNotNull() {
		for(TVBroadcastWithChannelInfo popular : tvPopularBroadcastsWithChannelInfo) {
				assertTrue(popular.areDataFieldsValid());
		}
	}
}
