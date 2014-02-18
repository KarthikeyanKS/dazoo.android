package com.mitv.test;

import junit.framework.Assert;

import org.junit.Test;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.models.gson.TVProgramAndChannelInfo;
import com.mitv.Consts;



public class TVBroadcastDetailsTest 
	extends TestCore 
{
	private static final String	TAG	= "TVBroadcastDetailsTest";
	
	private TVProgramAndChannelInfo tvBroadcastDetails;
	
	
	
	@Override
	protected void setUp() 
			throws Exception 
	{
		super.setUp();
		
		String channelId = "b24378bd-0a04-4427-814d-499b68eefd39";
		long beginTimeMillis = 1392737400000L;
		
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
			tvBroadcastDetails = new Gson().fromJson(responseString, TVProgramAndChannelInfo.class);
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