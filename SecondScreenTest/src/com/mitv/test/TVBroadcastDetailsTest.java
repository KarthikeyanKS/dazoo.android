package com.mitv.test;

import junit.framework.Assert;

import org.junit.Test;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.millicom.mitv.models.gson.TVBroadcastDetails;
import com.millicom.mitv.models.gson.TVChannel;
import com.mitv.Consts;



public class TVBroadcastDetailsTest 
	extends Tests 
{
	private static final String	TAG	= "TVBroadcastDetailsTest";
	
	private TVBroadcastDetails tvBroadcastDetails;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		String channelId = "b24378bd-0a04-4427-814d-499b68eefd39";
		long beginTimeMillis = 1392737400000L;
		
		StringBuilder sb = new StringBuilder();
		sb.append(Consts.URL_CHANNELS_ALL);
		sb.append(Consts.REQUEST_QUERY_SEPARATOR);
		sb.append(channelId);
		sb.append(Consts.API_BROADCASTS);
		sb.append(Consts.REQUEST_QUERY_SEPARATOR);
		sb.append(beginTimeMillis);
		
		String jsonString = super.deserializeJson(sb.toString());
				
		try
		{
			tvBroadcastDetails = new Gson().fromJson(jsonString, TVBroadcastDetails.class);
		}
		catch(JsonSyntaxException jsex)
		{
			Log.e(TAG, jsex.getMessage(), jsex);
		}
	}

	@Test
	public void testNotNull() {
		Assert.assertNotNull(tvBroadcastDetails);
	}
	
	@Test
	public void testAllVariablesNotNull() {
		
		TVChannel tvChannel = tvBroadcastDetails.getChannel();
		TVChannelGSONTest.testTVChannelObject(tvChannel);
		
		//Assert.assertNotNull(tvBroadcastDetails.getTvProgram());
	}

}
