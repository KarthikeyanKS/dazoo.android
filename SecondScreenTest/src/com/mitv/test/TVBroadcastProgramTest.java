
package com.mitv.test;



import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.models.gson.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.gson.TVChannel;
import com.mitv.Consts;



public class TVBroadcastProgramTest extends TestCore {
	
	private List<TVBroadcastWithChannelInfo> tvBroadcastsWithChannelInfo;
	private static final String	TAG	= "TVBroadcastProgramTest";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		String programId = "90958923-a34d-41d7-80fe-86f6c803f96a";
		
		StringBuilder url = new StringBuilder();
		url.append(Consts.URL_PROGRAMS);
		url.append(programId);
		url.append(Consts.API_BROADCASTS);
		
		HTTPCoreResponse httpCoreResponse = executeRequest(HTTPRequestTypeEnum.HTTP_GET, url.toString());
		
		String responseString = httpCoreResponse.getResponseString();
		
		try
		{
			tvBroadcastsWithChannelInfo = Arrays.asList(new Gson().fromJson(responseString, TVBroadcastWithChannelInfo[].class));
		}
		catch(JsonSyntaxException jsex)
		{
			Log.e(TAG, jsex.getMessage(), jsex);
		}
	}

	@Test
	public void testNotNull() {
		Assert.assertNotNull(tvBroadcastsWithChannelInfo);
	}
	
	@Test
	public void testAllVariablesNotNull() {
		for(TVBroadcastWithChannelInfo tvBroadcastWithChannelInfo : tvBroadcastsWithChannelInfo) {
			Assert.assertNotNull(tvBroadcastWithChannelInfo);
			
			TVBroadcastWithChannelInfoTest.testBroadcast(tvBroadcastWithChannelInfo);
			
			TVChannel tvChannel = tvBroadcastWithChannelInfo.getChannel();
			TVChannelGSONTest.testTVChannelObject(tvChannel);
		}
	}
}